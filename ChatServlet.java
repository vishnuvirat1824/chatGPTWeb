import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.json.*;

public class ChatServlet extends HttpServlet {
    // It's highly recommended to load API keys from environment variables or a secure configuration, not directly in code.
    // For this example, it's kept as is for demonstration purposes.
    // Replace with your actual Gemini API Key
    private static final String API_KEY = "YOUR_GEMINI_API_KEY"; // 🔑 IMPORTANT: Replace with your actual Gemini API Key

   @Override
   protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String userMessage = req.getParameter("message");
        resp.setContentType("text/plain");

        if (API_KEY.equals("YOUR_GEMINI_API_KEY")) {
            resp.setStatus(500);
            resp.getWriter().write("Error: Gemini API Key is not configured. Please replace 'YOUR_GEMINI_API_KEY' in ChatServlet.java.");
            return;
        }

        try {
            // Construct JSON request for Gemini API (e.g., Gemini 1.5 Flash)
            JSONObject requestJson = new JSONObject();

            // Gemini API uses 'contents' with 'role' and 'parts'
            JSONArray contents = new JSONArray();
            JSONObject userContent = new JSONObject();
            userContent.put("role", "user");
            JSONArray parts = new JSONArray();
            parts.put(new JSONObject().put("text", userMessage));
            userContent.put("parts", parts);
            contents.put(userContent);
            requestJson.put("contents", contents);

            // Optional: Add generation config or safety settings if needed
            // JSONObject generationConfig = new JSONObject();
            // generationConfig.put("temperature", 0.9);
            // generationConfig.put("topK", 1);
            // generationConfig.put("topP", 1);
            // requestJson.put("generationConfig", generationConfig);

            // HTTP POST to Gemini API
            // Using a specific model like 'gemini-1.5-flash-latest' or 'gemini-pro'
            // Ensure the region and model name are correct for your usage.
            String geminiApiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=" + API_KEY;
            HttpURLConnection connection = (HttpURLConnection) new URL(geminiApiUrl).openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestJson.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Check for HTTP response codes
            int responseCode = connection.getResponseCode();
            if (responseCode == 429) {
                resp.setStatus(429);
                resp.getWriter().write("Error: Too Many Requests. Please wait and try again.");
                return; // Exit the method after handling the 429 error
            } else if (responseCode >= 400) { // Handle other client-side errors (4xx)
                String errorResponse = "";
                try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                    errorResponse = errorReader.lines().reduce("", (accumulator, line) -> accumulator + line);
                } catch (Exception e) {
                    // Ignore, error stream might not be available or readable
                }
                System.err.println("Gemini API Error Response (" + responseCode + "): " + errorResponse);
                resp.setStatus(responseCode);
                resp.getWriter().write("Error from Gemini API: " + errorResponse);
                return;
            }


            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder responseText = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                responseText.append(line);
            }
            in.close();

            // Parse JSON response from Gemini
            JSONObject responseJson = new JSONObject(responseText.toString());
            String reply = "No response from AI."; // Default message

            // Gemini response structure: candidates[0].content.parts[0].text
            if (responseJson.has("candidates") && responseJson.getJSONArray("candidates").length() > 0) {
                JSONObject candidate = responseJson.getJSONArray("candidates").getJSONObject(0);
                if (candidate.has("content")) {
                    JSONObject content = candidate.getJSONObject("content");
                    if (content.has("parts") && content.getJSONArray("parts").length() > 0) {
                        JSONObject part = content.getJSONArray("parts").getJSONObject(0);
                        if (part.has("text")) {
                            reply = part.getString("text");
                        }
                    }
                }
            } else if (responseJson.has("promptFeedback")) {
                // Handle cases where no candidates are returned, e.g., due to safety settings
                JSONObject promptFeedback = responseJson.getJSONObject("promptFeedback");
                if (promptFeedback.has("blockReason")) {
                    reply = "Your prompt was blocked due to: " + promptFeedback.getString("blockReason");
                } else {
                    reply = "AI could not generate a response for this prompt.";
                }
            }


            resp.getWriter().write(reply.trim());

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            resp.getWriter().write("Error: " + e.getMessage());
        }
    }
}
