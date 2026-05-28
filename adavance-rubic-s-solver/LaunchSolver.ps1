# Launch script for Rubik's Cube Solver
Write-Host "Setting up Rubik's Cube Solver by Team Carlos..." -ForegroundColor Cyan

# Check for Java
$javaPath = "C:\Program Files\Java\jdk-25.0.2"
if (!(Test-Path $javaPath)) {
    # Fallback search if version differs
    $javaPath = Get-ChildItem "C:\Program Files\Java\jdk-*" | Select-Object -First 1 -ExpandProperty FullName
}

$env:JAVA_HOME = $javaPath
$env:Path = "$javaPath\bin;" + $env:Path

if (!(Get-Command java -ErrorAction SilentlyContinue)) {
    Write-Error "Java not found! Please install JDK 21 or higher."
    exit
}

# Download Maven Wrapper if not present
if (!(Test-Path "mvnw.cmd")) {
    Write-Host "Installing Maven Wrapper..." -ForegroundColor Yellow
    Invoke-WebRequest -Uri "https://raw.githubusercontent.com/takari/maven-wrapper/master/mvnw" -OutFile "mvnw"
    Invoke-WebRequest -Uri "https://raw.githubusercontent.com/takari/maven-wrapper/master/mvnw.cmd" -OutFile "mvnw.cmd"
    New-Item -ItemType Directory -Force -Path ".mvn/wrapper"
    Invoke-WebRequest -Uri "https://repo.maven.apache.org/maven2/io/takari/maven-wrapper/0.5.6/maven-wrapper-0.5.6.jar" -OutFile ".mvn/wrapper/maven-wrapper.jar"
    
    $wrapperProps = @"
distributionUrl=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.6/apache-maven-3.9.6-bin.zip
wrapperUrl=https://repo.maven.apache.org/maven2/io/takari/maven-wrapper/0.5.6/maven-wrapper-0.5.6.jar
"@
    $wrapperProps | Out-File ".mvn/wrapper/maven-wrapper.properties" -Encoding ascii
}

Write-Host "Building and Launching Application..." -ForegroundColor Green
.\mvnw.cmd javafx:run
