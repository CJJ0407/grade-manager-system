package com.cddx.www.grademanager.controller;

import com.cddx.www.grademanager.entity.pojo.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * AI 学习助手控制器。
 *
 * 功能项：
 * - 提供 AI 聊天页面：/ai/chat
 * - 提供 AI 问答接口：/ai/ask（AJAX 调用）
 *
 * 设计说明：
 * - 先命中“预制问题库”（本地快速回复）
 * - 再尝试调用 DeepSeek Chat Completions API
 * - 调用失败或未配置 Key 时，降级到本地默认智能回复
 */
@Controller
@RequestMapping("/ai")
public class AIController {

    private static final Logger log = LoggerFactory.getLogger(AIController.class);
    private final RestTemplate restTemplate = new RestTemplate();
    
    @Value("${ai.deepseek.api.url}")
    private String apiUrl;
    
    @Value("${ai.deepseek.api.token}")
    private String apiToken;
    
    @Value("${ai.deepseek.api.model}")
    private String model;

    /**
     * 显示AI答疑页面
     */
    @GetMapping("/chat")
    public String showAIChatPage(Model model, HttpSession session) {
        // 鉴权：必须登录后才能进入 AI 页面
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", currentUser);
        return "ai/chat";
    }

    /**
     * AI答疑接口 - 调用 DeepSeek API
     */
    @PostMapping("/ask")
    @ResponseBody
    public Map<String, Object> askAI(@RequestParam("question") String question,
                                    HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 鉴权：未登录不允许调用 AI 接口
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null) {
                response.put("status", "error");
                response.put("message", "请先登录");
                return response;
            }

            // 获取 AI 回复（内部包含：预制问题 -> DeepSeek -> 降级）
            String aiResponse = getAIResponse(question);
            response.put("status", "success");
            response.put("answer", aiResponse);
            response.put("question", question);
            
        } catch (Exception e) {
            // 异常兜底：避免前端一直 loading
            log.warn("AI 服务调用异常", e);
            response.put("status", "error");
            response.put("message", "AI服务暂时不可用，请稍后再试");
        }
        
        return response;
    }

    /**
     * 获取 AI 回复 - 使用 DeepSeek API
     */
    private String getAIResponse(String question) {
        // 1. 先检查是否是预制问题
        String presetResponse = checkPresetQuestions(question);
        if (presetResponse != null) {
            return presetResponse;
        }
        
        try {
            // 2. 检查 API token 是否已配置
            if (apiToken == null || apiToken.isEmpty() || apiToken.equals("your_deepseek_api_key_here")) {
                // 未配置 Key：直接走本地默认回复
                return getDefaultResponse(question);
            }
            
            // 3. 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            
            // 4. 构建消息列表
            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", question);
            messages.add(message);
            requestBody.put("messages", messages);
            
            // 5. 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiToken);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            // 6. 调用 DeepSeek Chat Completions API（OpenAI 兼容）
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                String responseBody = response.getBody();
                if (responseBody != null && !responseBody.isEmpty()) {
                    // 解析 DeepSeek/OpenAI 兼容 API 响应
                    return parseChatCompletionResponse(responseBody);
                }
            }
            
            // 6. 如果 API 调用失败，返回默认回复
            return getDefaultResponse(question);
            
        } catch (Exception e) {
            log.warn("DeepSeek API 调用失败", e);
            return getDefaultResponse(question);
        }
    }
    
    /**
     * 检查预制问题并返回对应答案
     * @param question 用户问题
     * @return 如果匹配到预制问题则返回答案，否则返回 null
     */
    private String checkPresetQuestions(String question) {
        String lowerQuestion = question.toLowerCase().trim();
        
        // ===== 预制问题库开始 =====
        
        // 数学计算类
        if (lowerQuestion.equals("1+1 等于几") || lowerQuestion.equals("1+1=?") || 
            lowerQuestion.equals("1 + 1 等于几") || lowerQuestion.equals("1+1")) {
            return "1 + 1 = 2";
        }
        if (lowerQuestion.equals("2+2 等于几") || lowerQuestion.equals("2+2=?") || 
            lowerQuestion.equals("2 + 2 等于几") || lowerQuestion.equals("2+2")) {
            return "2 + 2 = 4";
        }
        if (lowerQuestion.equals("3+3 等于几") || lowerQuestion.equals("3+3=?") || 
            lowerQuestion.equals("3 + 3 等于几") || lowerQuestion.equals("3+3")) {
            return "3 + 3 = 6";
        }
        if (lowerQuestion.equals("5+5 等于几") || lowerQuestion.equals("5+5=?") || 
            lowerQuestion.equals("5 + 5 等于几") || lowerQuestion.equals("5+5")) {
            return "5 + 5 = 10";
        }
        if (lowerQuestion.equals("10+10 等于几") || lowerQuestion.equals("10+10=?") || 
            lowerQuestion.equals("10 + 10 等于几") || lowerQuestion.equals("10+10")) {
            return "10 + 10 = 20";
        }
        
        // 问候语类
        if (lowerQuestion.equals("你好") || lowerQuestion.equals("hello") || 
            lowerQuestion.equals("hi") || lowerQuestion.equals("您好")) {
            return "你好！我是 AI 学习助手，很高兴为您服务！有什么问题尽管问我吧！";
        }
        if (lowerQuestion.equals("早上好") || lowerQuestion.equals("早安")) {
            return "早上好！祝您今天学习愉快！";
        }
        if (lowerQuestion.equals("晚上好") || lowerQuestion.equals("晚安")) {
            return "晚上好！注意休息，不要熬夜学习哦！";
        }
        
        // 系统功能类
        if (lowerQuestion.equals("怎么查看我的成绩") || lowerQuestion.equals("成绩查询") || 
            lowerQuestion.equals("我的成绩在哪里看")) {
            return "您可以在主页点击\"我的成绩\"模块，那里会显示您的所有考试成绩和详细信息。";
        }
        if (lowerQuestion.equals("怎么添加考试") || lowerQuestion.equals("如何录入考试成绩")) {
            return "在主页点击\"考试管理\"，然后选择\"添加考试\"，填写相关信息后保存即可。";
        }
        if (lowerQuestion.equals("怎么修改成绩") || lowerQuestion.equals("成绩录错了怎么办")) {
            return "在\"成绩管理\"模块中找到对应的成绩记录，点击\"编辑\"按钮进行修改。修改后系统会自动保存修改记录。";
        }
        if (lowerQuestion.equals("怎么退出登录")) {
            return "点击页面右上角的\"退出登录\"按钮即可安全退出系统。";
        }
        
        // 时间日期类
        if (lowerQuestion.equals("现在几点了") || lowerQuestion.equals("现在什么时间了")) {
            return "现在的时间是：" + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy 年 MM 月 dd 日 HH:mm:ss"));
        }
        if (lowerQuestion.equals("今天几号") || lowerQuestion.equals("今天是什么日期")) {
            return "今天是：" + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy 年 MM 月 dd 日"));
        }
        
        // 学习建议类
        if (lowerQuestion.equals("如何提高学习成绩") || lowerQuestion.equals("学习方法有哪些")) {
            return "提高学习成绩的方法：\n1. 制定合理的学习计划\n2. 做好课前预习和课后复习\n3. 多做练习题巩固知识\n4. 遇到不懂的及时请教\n5. 保持良好的作息和心态";
        }
        if (lowerQuestion.equals("怎么记住英语单词") || lowerQuestion.equals("英语单词怎么背")) {
            return "记忆英语单词的技巧：\n1. 词根词缀法：理解单词构成\n2. 联想记忆：创造有趣联想\n3. 反复复习：遵循遗忘曲线\n4. 语境记忆：在句子中记忆\n5. 多听多说：强化音形联系";
        }
        
        // 生活常识类
        if (lowerQuestion.equals("感冒了怎么办") || lowerQuestion.equals("感冒吃什么")) {
            return "感冒建议：\n1. 多喝水，保持充足休息\n2. 饮食清淡，多吃蔬菜水果\n3. 注意保暖，避免受凉\n4. 如症状严重或持续，请及时就医";
        }
        if (lowerQuestion.equals("失眠怎么办") || lowerQuestion.equals("睡不着觉")) {
            return "改善睡眠的建议：\n1. 保持规律作息，固定睡觉时间\n2. 睡前避免使用电子设备\n3. 可以喝杯温牛奶\n4. 保持卧室安静、黑暗\n5. 如长期失眠，建议咨询医生";
        }
        
        // 技术问题类
        if (lowerQuestion.equals("电脑开不了机怎么办") || lowerQuestion.equals("电脑无法启动")) {
            return "电脑无法启动的解决步骤：\n1. 检查电源连接是否正常\n2. 检查显示器是否开启\n3. 尝试重启电脑\n4. 如仍无法启动，联系技术支持";
        }
        if (lowerQuestion.equals("网络连接不上怎么办")) {
            return "网络连接问题解决步骤：\n1. 检查网线/WiFi 是否连接\n2. 重启路由器\n3. 检查网络设置\n4. 联系网络服务提供商";
        }
        
        // 感谢类
        if (lowerQuestion.equals("谢谢") || lowerQuestion.equals("谢谢你") || 
            lowerQuestion.equals("thanks") || lowerQuestion.equals("thank you")) {
            return "不客气！我很高兴能帮助到您！如果还有其他问题，随时可以问我哦！";
        }
        
        // 再见类
        if (lowerQuestion.equals("再见") || lowerQuestion.equals("拜拜") || 
            lowerQuestion.equals("bye") || lowerQuestion.equals("byebye")) {
            return "再见！祝您学习进步，生活愉快！有任何问题随时欢迎回来问我！";
        }
        
        // ===== 预制问题库结束 =====
        
        // 如果没有匹配到预制问题，返回 null，继续后续处理
        return null;
    }

    /**
     * 解析 DeepSeek/OpenAI 兼容的 Chat Completions API 响应
     */
    private String parseChatCompletionResponse(String responseBody) {
        try {
            // 简单的JSON解析，提取choices[0].message.content
            if (responseBody.contains("\"choices\"")) {
                int choicesStart = responseBody.indexOf("\"choices\"");
                if (choicesStart != -1) {
                    int contentStart = responseBody.indexOf("\"content\":\"", choicesStart);
                    if (contentStart != -1) {
                        contentStart += 11; // 跳过 "content":"
                        int contentEnd = responseBody.indexOf("\"", contentStart);
                        if (contentEnd > contentStart) {
                            String content = responseBody.substring(contentStart, contentEnd);
                            // 处理转义字符
                            content = content.replace("\\n", "\n")
                                           .replace("\\\"", "\"")
                                           .replace("\\\\", "\\");
                            return content;
                        }
                    }
                }
            }
            
            // 如果解析失败，返回默认回复
            return "抱歉，AI回复解析失败，请稍后再试。";
            
        } catch (Exception e) {
            log.warn("AI 回复解析失败", e);
            return "抱歉，AI回复解析失败，请稍后再试。";
        }
    }

    /**
     * 智能回复 - 支持各种类型的问题
     */
    private String getDefaultResponse(String question) {
        String lowerQuestion = question.toLowerCase().trim();
        
        // 数学计算
        if (lowerQuestion.contains("+") || lowerQuestion.contains("加") || lowerQuestion.contains("等于")) {
            return handleMathQuestion(question);
        }
        
        // 系统相关问题
        if (lowerQuestion.contains("成绩") || lowerQuestion.contains("分数")) {
            return "关于成绩查询，您可以在系统中查看\"我的成绩\"模块，那里会显示您的所有考试成绩和详细信息。";
        } else if (lowerQuestion.contains("考试") || lowerQuestion.contains("测试")) {
            return "考试相关信息可以在\"我的考试\"模块中查看，包括考试时间、科目等信息。";
        } else if (lowerQuestion.contains("科目") || lowerQuestion.contains("课程")) {
            return "科目信息可以在\"我的科目\"模块中查看，包括课程名称、学分等详细信息。";
        } else if (lowerQuestion.contains("登录") || lowerQuestion.contains("密码")) {
            return "如果遇到登录问题，请检查用户名和密码是否正确，或联系系统管理员。";
        }
        
        // 问候语
        if (lowerQuestion.contains("你好") || lowerQuestion.contains("hello") || lowerQuestion.contains("hi")) {
            return "你好！我是基于 DeepSeek AI 的智能学习助手，现在变得更聪明了！我可以帮您解答各种问题，包括学习、编程、生活等各个方面。有什么问题尽管问我吧！";
        }
        
        // 时间相关
        if (lowerQuestion.contains("时间") || lowerQuestion.contains("几点")) {
            return "现在的时间是：" + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss"));
        }
        
        // 天气相关
        if (lowerQuestion.contains("天气")) {
            return "抱歉，我暂时无法获取实时天气信息。建议您查看天气预报应用或网站获取准确的天气信息。";
        }
        
        // 学习建议
        if (lowerQuestion.contains("学习") || lowerQuestion.contains("怎么学") || lowerQuestion.contains("方法")) {
            return "学习建议：\n1. 制定学习计划，合理安排时间\n2. 多做练习题，巩固知识点\n3. 及时复习，避免遗忘\n4. 保持积极的学习态度\n5. 遇到问题及时寻求帮助";
        }
        
        // 编程相关
        if (lowerQuestion.contains("编程") || lowerQuestion.contains("代码") || lowerQuestion.contains("java") || lowerQuestion.contains("python")) {
            return "编程学习建议：\n1. 从基础语法开始学习\n2. 多动手写代码，实践出真知\n3. 阅读优秀的开源项目\n4. 参与编程社区讨论\n5. 持续学习新技术";
        }
        
        // 生活常识
        if (lowerQuestion.contains("生活") || lowerQuestion.contains("健康") || lowerQuestion.contains("饮食")) {
            return "生活小贴士：\n1. 保持规律作息，早睡早起\n2. 均衡饮食，多吃蔬菜水果\n3. 适量运动，增强体质\n4. 保持心情愉快，减少压力\n5. 定期体检，关注健康";
        }
        
        // 技术问题
        if (lowerQuestion.contains("电脑") || lowerQuestion.contains("软件") || lowerQuestion.contains("系统")) {
            return "技术问题解决建议：\n1. 重启设备，解决临时问题\n2. 检查网络连接\n3. 更新软件到最新版本\n4. 清理系统垃圾文件\n5. 如问题持续，联系技术支持";
        }
        
        // 默认智能回复
        return getSmartDefaultResponse(question);
    }
    
    /**
     * 处理数学问题
     */
    private String handleMathQuestion(String question) {
        try {
            // 简单的数学计算处理
            if (question.contains("1+1") || question.contains("1 + 1")) {
                return "1 + 1 = 2";
            } else if (question.contains("2+2") || question.contains("2 + 2")) {
                return "2 + 2 = 4";
            } else if (question.contains("3+3") || question.contains("3 + 3")) {
                return "3 + 3 = 6";
            } else if (question.contains("4+4") || question.contains("4 + 4")) {
                return "4 + 4 = 8";
            } else if (question.contains("5+5") || question.contains("5 + 5")) {
                return "5 + 5 = 10";
            } else if (question.contains("10+10") || question.contains("10 + 10")) {
                return "10 + 10 = 20";
            } else if (question.contains("100+100") || question.contains("100 + 100")) {
                return "100 + 100 = 200";
            }
            
            // 尝试解析简单的数学表达式
            String cleanQuestion = question.replaceAll("[^0-9+\\-*/=]", "").trim();
            if (cleanQuestion.contains("+") && cleanQuestion.contains("=")) {
                String[] parts = cleanQuestion.split("=");
                if (parts.length == 2) {
                    String left = parts[0].trim();
                    
                    if (left.contains("+")) {
                        String[] numbers = left.split("\\+");
                        if (numbers.length == 2) {
                            try {
                                int a = Integer.parseInt(numbers[0].trim());
                                int b = Integer.parseInt(numbers[1].trim());
                                int result = a + b;
                                return a + " + " + b + " = " + result;
                            } catch (NumberFormatException e) {
                                // 忽略解析错误
                            }
                        }
                    }
                }
            }
            
            return "这是一个数学问题！我可以帮您计算简单的加法。比如：1+1=2, 2+2=4, 3+3=6 等等。";
            
        } catch (Exception e) {
            return "数学计算很有趣！我可以帮您计算简单的加法运算。";
        }
    }
    
    /**
     * 智能默认回复
     */
    private String getSmartDefaultResponse(String question) {
        // 根据问题长度和内容给出不同的回复
        if (question.length() < 5) {
            return "您的问题比较简短，能否详细描述一下？我很乐意帮助您！";
        } else if (question.length() > 100) {
            return "您的问题很详细！虽然我主要专注于学习相关的问题，但我会尽力为您提供有用的信息。";
        } else {
            return "这是一个很有趣的问题！虽然我主要帮助解决学习相关的问题，但我也会尽力回答其他问题。您可以问我关于：\n" +
                   "• 数学计算（如：1+1等于几）\n" +
                   "• 学习方法和建议\n" +
                   "• 编程相关问题\n" +
                   "• 生活小贴士\n" +
                   "• 系统使用帮助\n" +
                   "• 时间查询等\n" +
                   "有什么具体想了解的吗？";
        }
    }

    /**
     * 显示AI帮助页面
     */
    @GetMapping("/help")
    public String showAIHelpPage(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", currentUser);
        return "ai/help";
    }
}
