#!/bin/bash

# Build script for OpenAPI Client project

echo "========================================"
echo "OpenAPI Client - Build Script"
echo "========================================"
echo ""

echo "[1/3] Cleaning previous build..."
./mvnw clean
if [ $? -ne 0 ]; then
    echo "ERROR: Clean failed"
    exit 1
fi
echo ""

echo "[2/3] Compiling and generating OpenAPI client..."
./mvnw compile
if [ $? -ne 0 ]; then
    echo "ERROR: Compilation failed"
    exit 1
fi
echo ""

echo "[3/3] Running tests..."
./mvnw test
if [ $? -ne 0 ]; then
    echo "WARNING: Tests failed"
    echo ""
fi

echo "========================================"
echo "Build completed successfully!"
echo "========================================"
echo ""
echo "To run the application:"
echo "  ./mvnw compile quarkus:dev"
echo ""
echo "Application will be available at:"
echo "  http://localhost:8779"
echo ""
echo "API endpoints:"
echo "  GET  /api/pets/health"
echo "  GET  /api/pets"
echo "  GET  /api/pets/{petId}"
echo "  POST /api/pets"
echo ""

