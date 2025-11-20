package com.travelplanner.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelplanner.config.XunfeiConfig;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.ByteString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * 讯飞语音听写服务
 * 实现与讯飞API的WebSocket通信
 */
@Service
@Slf4j
public class SpeechRecognitionService {
    // 讯飞API配置
    @Autowired
    private XunfeiConfig xunfeiConfig;

    // WebSocket客户端
    private OkHttpClient client;
    private WebSocket webSocket;

    // 结果回调
    private Consumer<String> resultCallback;
    private Consumer<String> errorCallback;

    // 同步控制
    private CountDownLatch latch;

    // 识别结果
    private String resultText;
    // 是否已结束
    private boolean isEnded = false;
    private boolean isConfigured = false;
    private final Queue<byte[]> audioQueue = new LinkedList<>();

    // ObjectMapper
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 创建服务实例
     */
    public SpeechRecognitionService createInstance() {
        SpeechRecognitionService instance = new SpeechRecognitionService();
        instance.xunfeiConfig = this.xunfeiConfig;
        instance.client = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .build();
        return instance;
    }

    /**
     * 开始语音识别
     */
    public void startRecognition(Consumer<String> resultCallback, Consumer<String> errorCallback) {
        this.resultCallback = resultCallback;
        this.errorCallback = errorCallback;

        this.isEnded = false;
        this.resultText = "";
        this.latch = new CountDownLatch(1);

        try {
            // 构建鉴权URL
            String authUrl = getAuthUrl();

            // 创建WebSocket请求
            Request request = new Request.Builder().url(authUrl).build();

            // 创建WebSocket监听器
            WebSocketListener listener = new WebSocketListener() {
                @Override
                public void onOpen(WebSocket webSocket, Response response) {
                    log.info("与讯飞语音听写webapi服务器的WebSocket连接已建立");
                    SpeechRecognitionService.this.webSocket = webSocket;

                    // 发送开始参数
                    sendStartParams();
                }

                @Override
                public void onMessage(WebSocket webSocket, String text) {
                    log.debug("收到消息: {}", text);
                    handleMessage(text);
                }

                @Override
                public void onMessage(WebSocket webSocket, ByteString bytes) {
                    // 处理二进制消息（如果有）
                }

                @Override
                public void onClosing(WebSocket webSocket, int code, String reason) {
                    log.info("与讯飞语音听写webapi服务器的WebSocket连接正在关闭: {}, {}", code, reason);
                }

                @Override
                public void onClosed(WebSocket webSocket, int code, String reason) {
                    log.info("与讯飞语音听写webapi服务器的WebSocket连接已关闭: {}, {}", code, reason);
                    latch.countDown();
                }

                @Override
                public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                    log.error("与讯飞语音听写webapi服务器的WebSocket连接失败", t);
                    if (errorCallback != null) {
                        errorCallback.accept("连接失败: " + t.getMessage());
                    }
                    latch.countDown();
                }
            };

            // 建立WebSocket连接
            webSocket = client.newWebSocket(request, listener);
        } catch (Exception e) {
            log.error("开始语音识别时发生错误", e);
            if (errorCallback != null) {
                errorCallback.accept("开始语音识别失败: " + e.getMessage());
            }
        }
    }

    /**
     * 发送音频数据
     */
    public void sendAudioData(byte[] audioData) {
        if (webSocket == null || isEnded) {
            log.warn("WebSocket未连接或已结束, 忽略音频数据");
            return;
        }

        try {
            if (isConfigured) {
                // 配置完成，直接发送
                sendAudioMessage(audioData, 1);
            } else {
                // 配置未完成，将音频数据加入队列
                audioQueue.add(audioData);
                log.info("音频数据已加入队列，当前队列大小: {}", audioQueue.size());
            }
        } catch (Exception e) {
            log.error("发送音频数据时发生错误", e);
            if (errorCallback != null) {
                errorCallback.accept("发送音频数据失败: " + e.getMessage());
            }
        }
    }

    private void sendAudioMessage(byte[] audioData, int status) {
        try {
            // 构建音频数据消息
            var dataMap = new HashMap<>();
            dataMap.put("status", status);
            if (audioData != null) {
                dataMap.put("audio", Base64.getEncoder().encodeToString(audioData));
            }

            var message = new HashMap<>();
            message.put("data", dataMap);

            // 发送消息
            var jsonMessage = objectMapper.writeValueAsString(message);
            webSocket.send(jsonMessage);
        } catch (Exception e) {
            log.error("发送音频消息时发生错误", e);
            if (errorCallback != null) {
                errorCallback.accept("发送音频消息失败: " + e.getMessage());
            }
        }
    }

    private void sendQueuedAudio() {
        log.info("开始发送队列中的音频数据，队列大小: {}", audioQueue.size());
        while (!audioQueue.isEmpty() && !isEnded) {
            byte[] audioData = audioQueue.poll();
            sendAudioMessage(audioData, 1);
        }
    }

    /**
     * 结束语音识别
     */
    public void endRecognition() {
        if (webSocket == null || isEnded) {
            log.warn("WebSocket未连接或已结束, 忽略结束请求");
            return;
        }

        try {
            // 发送队列中的音频数据
            sendQueuedAudio();
            // 发送结束消息
            sendAudioMessage(null, 2);

            isEnded = true;
            // 等待连接关闭
            latch.await(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("结束语音识别时发生错误", e);
            if (errorCallback != null) {
                errorCallback.accept("结束语音识别失败: " + e.getMessage());
            }
        }
    }

    /**
     * 清理资源
     */
    public void cleanup() {
        if (webSocket != null) {
            webSocket.close(1000, "正常关闭");
            webSocket = null;
        }

        if (client != null) {
            client.dispatcher().executorService().shutdown();
            client = null;
        }
    }

    /**
     * 发送开始参数
     */
    private void sendStartParams() {
        try {
            // 构建开始参数
            var commonParams = new HashMap<>();
            commonParams.put("app_id", xunfeiConfig.getAppId());
            var businessParams = new HashMap<>();
            businessParams.put("language", "zh_cn");
            businessParams.put("domain", "iat");
            businessParams.put("accent", "mandarin");
            businessParams.put("vad_eos", 5000);
            businessParams.put("dwa", "wpgs");
            var dataParams = new HashMap<>();
            dataParams.put("status", 0); // 0表示首帧
            dataParams.put("format", "audio/L16;rate=16000");
            dataParams.put("encoding", "raw");
            var message = new HashMap<>();
            message.put("common", commonParams);
            message.put("business", businessParams);
            message.put("data", dataParams);
            // 发送消息
            String jsonMessage = objectMapper.writeValueAsString(message);
            webSocket.send(jsonMessage);
            // 设置配置完成标志
            isConfigured = true;
            // 发送队列中的音频数据
            sendQueuedAudio();
        } catch (Exception e) {
            log.error("发送开始参数时发生错误", e);
            if (errorCallback != null) {
                errorCallback.accept("发送开始参数失败: " + e.getMessage());
            }
        }
    }

    /**
     * 处理收到的消息
     */
    private void handleMessage(String message) {
        try {
            JsonNode root = objectMapper.readTree(message);
            
            // 检查是否有错误
            if (root.has("code") && root.get("code").asInt() != 0) {
                String errorMsg = root.get("message").asText();
                log.error("语音识别错误: {}", errorMsg);
                if (errorCallback!= null) errorCallback.accept("语音识别错误: " + errorMsg);
                return;
            }
            
            // 卫语句：检查结果结构是否完整
            if (!root.has("data")) return;
            JsonNode dataNode = root.get("data");
            if (!dataNode.has("result")) return;
            JsonNode resultNode = dataNode.get("result");
            if (!resultNode.has("ws")) return;
            if (!dataNode.has("status")) return;
            
            JsonNode wsNode = resultNode.get("ws");
            StringBuilder text = new StringBuilder();
            
            // 拼接识别文本
            for (JsonNode ws : wsNode) {
                if (!ws.has("cw")) continue;
                JsonNode cwArray = ws.get("cw");
                if (cwArray.size() == 0) continue;
                JsonNode cwNode = cwArray.get(0);
                if (cwNode.has("w")) {
                    text.append(cwNode.get("w").asText());
                }
            }

            log.info("收到识别结果: {}", text.toString());
            
            // 处理识别结果
            int status = dataNode.get("status").asInt();
            String currentResult = text.toString();
            
            if (status == 1) {
                // 中间结果，覆盖当前累积内容
                resultText = currentResult;
                if (resultCallback != null) {
                    resultCallback.accept(currentResult);
                }
            } else if (status == 2) {
                // 最终结果，追加到累积内容并返回完整结果
                resultText += currentResult;
                if (resultCallback != null) {
                    resultCallback.accept(resultText);
                }
            }
        } catch (Exception e) {
            log.error("处理消息时发生错误", e);
            if (errorCallback != null) {
                errorCallback.accept("处理消息失败: " + e.getMessage());
            }
        }
    }

    /**
     * 构建鉴权URL
     */
    private String getAuthUrl() throws Exception {
        URL url = HttpUrl.parse(xunfeiConfig.getHost()).url();

        // 获取当前时间
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = format.format(new Date());

        // 拼接字符串
        String signatureOrigin = String.format("host: %s\ndate: %s\nGET %s HTTP/1.1", url.getHost(), date,
                url.getPath());

        // 使用hmac-sha256进行加密
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(xunfeiConfig.getApiSecret().getBytes(StandardCharsets.UTF_8),
                "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] signatureBytes = mac.doFinal(signatureOrigin.getBytes(StandardCharsets.UTF_8));
        String signature = Base64.getEncoder().encodeToString(signatureBytes);

        // 构建authorization
        String authorizationOrigin = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"",
                xunfeiConfig.getApiKey(), "hmac-sha256", "host date request-line", signature);

        String authorization = Base64.getEncoder().encodeToString(authorizationOrigin.getBytes(StandardCharsets.UTF_8));

        // 构建最终URL
        String authUrl = String.format("%s?authorization=%s&date=%s&host=%s", xunfeiConfig.getHost(), authorization,
                date, url.getHost());

        // 将HTTP/HTTPS协议转换为WebSocket协议
        authUrl = authUrl.replace("http://", "ws://").replace("https://", "wss://");

        return authUrl;
    }
}