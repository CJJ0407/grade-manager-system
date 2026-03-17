# DeepSeek AI 配置说明

## 概述
本系统使用 **DeepSeek API** 提供 AI 问答功能。DeepSeek 提供与 OpenAI 兼容的 Chat Completions 接口，请求与响应格式与硅基流动等兼容。

## 配置步骤

### 1. 获取 API Key
1. 访问 [DeepSeek 开放平台](https://platform.deepseek.com/)
2. 注册账号并登录
3. 在控制台创建 API Key
4. 复制您的 API Key（格式类似 `sk-...`）

### 2. 配置系统
1. 打开 `src/main/resources/application.properties`
2. 找到以下配置并填入您的 Key：

```properties
# DeepSeek AI API 配置
ai.deepseek.api.url=https://api.deepseek.com/v1/chat/completions
ai.deepseek.api.token=your_deepseek_api_key_here
ai.deepseek.api.model=deepseek-chat
```

3. 将 `your_deepseek_api_key_here` 替换为您的实际 API Key。

### 3. 可选模型
- `deepseek-chat`：通用对话模型（推荐）
- `deepseek-reasoner`：思考模式，适合复杂推理

### 4. 重启应用
配置完成后，重启 Spring Boot 应用即可使用 AI 功能。

---

## 手动测试 API（可选）

### cURL 模板
```bash
curl https://api.deepseek.com/v1/chat/completions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -d "{\"model\":\"deepseek-chat\",\"messages\":[{\"role\":\"user\",\"content\":\"你好\"}],\"stream\":false}"
```

### Node.js 模板（仅供参考）
```javascript
const response = await fetch('https://api.deepseek.com/v1/chat/completions', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': 'Bearer YOUR_API_KEY'
  },
  body: JSON.stringify({
    model: 'deepseek-chat',
    messages: [{ role: 'user', content: '你好' }],
    stream: false
  })
});
const data = await response.json();
console.log(data.choices[0].message.content);
```

本项目中实际调用由 **Java Spring Boot** 的 `RestTemplate` 完成，无需单独写 Node 或 curl，以上仅用于在浏览器或命令行快速验证 Key 是否可用。

---

## 功能特性

- **智能问答**：基于 DeepSeek 大模型
- **学习与编程**：适合解答学习、编程类问题
- **降级策略**：未配置或 API 失败时，使用内置智能回复

## 注意事项

1. **API 费用**：DeepSeek 按量计费，请合理使用
2. **网络**：需能访问 `api.deepseek.com`
3. **Token 安全**：不要将 API Key 提交到公开仓库
4. **备用**：未配置或调用失败时，系统会使用内置回复

## 故障排除

- **无回复/报错**：检查 `ai.deepseek.api.token` 是否正确
- **超时**：检查网络与防火墙
- **解析错误**：多为临时问题，重试即可

## 技术细节

请求体与 OpenAI 兼容，例如：

```json
{
  "model": "deepseek-chat",
  "messages": [
    { "role": "user", "content": "用户问题" }
  ],
  "stream": false
}
```

响应中取 `choices[0].message.content` 即为 AI 回复文本。
