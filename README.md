# Spring Boot Groq Cloud Integration

This is a simple Spring Boot application (compatible with Java 8) that demonstrates how to integrate with the Groq Cloud API to send prompts and receive responses using `RestTemplate`.

## Prerequisites

- Java 8 or later
- Maven
- A Groq Cloud account and an API Key. You can get one for free from [https://console.groq.com/keys](https://console.groq.com/keys).

## Configuration

The application configuration is in `src/main/resources/application.properties`.

1.  `groq.api.url`: The URL for the Groq Cloud API.
2.  `groq.model`: The model to use (e.g., `llama3-8b-8192`).
3.  `groq.api.key`: **Your secret API key**. Paste your key here to get started quickly.

**Security Note:** For any real application, it is strongly recommended to load the API key from an environment variable or a secret manager instead of hardcoding it in the properties file.

## How to Run

1.  Navigate to the `ollama-spring-boot` directory.
2.  Run the application using Maven:
    ```bash
    mvn spring-boot:run
    ```
3.  The application will start on port `8080`.

## API Endpoint

You can send prompts to the application via a POST request.

- **URL:** `http://localhost:8080/api/prompt`
- **Method:** `POST`
- **Headers:** `Content-Type: text/plain`
- **Body:** Your prompt as a plain text string.

### Example using cURL

Make sure the application is running and you have set your API key in `application.properties`.

```bash
curl -X POST http://localhost:8081/api/prompt \
-H "Content-Type: text/plain" \
-d "Why is the sky blue?"
```

### Example Response

```json
{
  "response": "The sky appears blue because of a phenomenon called Rayleigh scattering..."
}
```
