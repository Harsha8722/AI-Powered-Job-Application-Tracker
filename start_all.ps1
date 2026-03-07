Write-Host "========================================="
Write-Host "Starting AI Powered Job Application Tracker"
Write-Host "========================================="

# Start AI Service
Write-Host "1. Starting Python AI Service (Port 5000)..."
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd ai-service; python app.py"

# Start Spring Boot Backend
Write-Host "2. Starting Spring Boot Backend (Port 8080)..."
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd backend; mvn spring-boot:run"

# Start React Frontend
Write-Host "3. Starting React Frontend (Port 3000)..."
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd frontend; npm start"

Write-Host "========================================="
Write-Host "All services are starting up in separate windows!"
Write-Host "- React Frontend: http://localhost:3000"
Write-Host "- Spring Boot API: http://localhost:8080/swagger-ui.html"
Write-Host "- AI Service: http://localhost:5000/health"
Write-Host "========================================="
