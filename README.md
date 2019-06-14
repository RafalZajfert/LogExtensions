# LogExtensions

## Gradle Dependency (jitpack.io)

project level _build.gradle_
```Gradle
allprojects {
    repositories {
        ...
	maven { url 'https://jitpack.io' }
    }
}
```

app _build.gradle_
```Gradle
dependencies {
    implementation 'software.rsquared:log-extensions:1.0.3'
}
```
[![](https://jitpack.io/v/software.rsquared/log-extensions.svg)](https://jitpack.io/#software.rsquared/log-extensions)
