#!/bin/bash

echo "🚀 Building and starting file-extension-blocker application..."

# Build and start containers
docker-compose up --build -d

echo "✅ Application is starting up..."
echo "🌐 Backend will be available at: http://localhost:8080"
echo "🗄️ Database will be available at: localhost:5432"
echo ""
echo "📋 To view logs:"
echo "   docker-compose logs -f app"
echo ""
echo "🛑 To stop:"
echo "   docker-compose down"
echo ""
echo "🧹 To clean up (remove volumes):"
echo "   docker-compose down -v" 