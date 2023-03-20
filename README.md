# Macaroni
'Macaroni' can be used to handle loading data from both local and remote sources, as well as caching the data locally.

## Download
Macaroni is available for download via JitPack.

The most recent version is 1.0.0.

use gradle:
```
repositories {
  mavenCentral()
  maven { url "https://jitpack.io" }
}

dependencies {
  implementation 'com.github.GSM-MSG:Macaroni:${version}'
}
```

use gradle.kts
```
repositories {
  mavenCentral()
  maven { url = uri("https://jitpack.io") }
}

dependencies {
  implementation("com.github.GSM-MSG:Macaroni:${version}")
}
```

## How do i use Macaroni
