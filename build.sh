#!/bin/bash

echo "Building WorkNix..."

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "Maven is not installed. Please install Maven first."
    exit 1
fi

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Java is not installed. Please install Java JDK first."
    exit 1
fi

# Clean previous builds
echo "Cleaning previous builds..."
mvn clean

# Compile and package
echo "Compiling and packaging..."
mvn package

# Check if build was successful
if [ $? -eq 0 ]; then
    echo "Build successful!"
    echo "JAR file created at: target/worknix-1.0-SNAPSHOT.jar"
    echo ""
    echo "To run WorkNix, use:"
    echo "java -jar target/worknix-1.0-SNAPSHOT.jar"
else
    echo "Build failed. Please check the error messages above."
    exit 1
fi 