# Stock Helper

Spring Boot application that processes uploaded images by:
- Resizing them
- Generating AI-based descriptions and keywords (via OpenAI)
- Updating XMP metadata with the generated data
- Returning a ZIP archive with the updated images

## Project Structure

```
src/main/java/com/example/stockhelper
│
├── api/controller
│   └── ImageController.java         # Handles HTTP requests for uploading and downloading images
│
├── application
│   ├── DescribeAndTagImageService   # Core service orchestrating the image processing pipeline
│   ├── exceptions                   # Custom exceptions (e.g., XmpUpdateException)
│   ├── ports                        # Hexagonal architecture ports
│   │   ├── in                       # Use cases (interfaces to be called by controllers)
│   │   └── out                      # Interfaces for external services (resizer, archiver, AI, etc.)
│   └── validators                   # Validates AI-generated metadata before embedding
│
├── domain/model
│   ├── ImageRequest.java            # Represents an uploaded image
│   └── ImageDescription.java        # Represents AI-generated description, title, and keywords
│
├── infrastructure
│   ├── adapters                     # Implementations of ports (resizer, OpenAI, XMP updater, ZIP)
│   └── configs                      # Beans and configuration (e.g., prompt setup)
│
└── utils
    └── ImageUtils.java              # Helper methods for image scaling and transformations
```

## Tech Stack
- Java 17
- Spring Boot 3.5.5
- Maven
- Thymeleaf (basic frontend)
- Spring AI (OpenAI integration)
- Apache Commons Imaging & PDFBox XMPBox (metadata)
- Lombok

## Requirements
- JDK 17+
- Maven 3.9+
- OpenAI API key (see below)

## Getting an OpenAI API Key
1. Create an account at [OpenAI](https://platform.openai.com/).
2. Go to the [API Keys section](https://platform.openai.com/account/api-keys).
3. Click **Create new secret key**.
4. Copy the generated key and store it securely.
5. Use it in your environment variables as shown below.

## Configuration
Set your API key as an environment variable:

```bash
export OPENAI_API_KEY=your_api_key_here
```

or in PowerShell:

```powershell
$env:OPENAI_API_KEY="your_api_key_here"
```

## Run Locally

Clone the repository:

```bash
git clone https://github.com/your-username/stock-helper.git
cd stock-helper
```

Build and run with Maven:

```bash
mvn spring-boot:run
```

The app will start at [http://localhost:8080](http://localhost:8080).

## Usage
1. Open `http://localhost:8080` in your browser.
2. Upload one or more images.
3. Wait for processing.
4. Download the returned `images.zip`.

