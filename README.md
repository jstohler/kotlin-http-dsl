
# Kotlin-HTTP-DSL

### What is this?
This project provides a simple DSL (Domain-Specific Language) for HTTP requests within Kotlin projects.

### Requirements:
This project targets JVM 11, which provides a much better HTTP Client for synch/asynch requests. I may add support 
for older JVM versions (using http-client packages, such as [khttp](https://github.com/jkcclemens/khttp)).

### Is this maintained?
Yes, while this project will be built out some more, this is an early version of the design. Nothing is set in stone, 
and updates may drastically change the structure of the project, as this is **Version: 0.0.2**. 

If this is something that is useful to you, I will be more than happy to take PRs and care for backwards-compatibility,
once the project is at a stable state!


### Examples:

Get:
```kotlin
    val request = RequestBuilderImpl.build {
        get("https://jstohler.com")
    }
```

Get with Headers:
```kotlin
    val request = RequestBuilderImpl.build {
        get("https://jstohler.com")
        header { "Authorization: Bearer some_token" }
        header { "Content-Type: application/text" }
    }

    // Or
    val request = RequestBuilderImpl.build {
        get("https://jstohler.com")
        headers { listOf("Authorization: Bearer some_token", "Content-Type: application/text") }
    }
```

Post: 
```kotlin
    val request = RequestBuilderImpl.build {
        post("https://jstohler.com") {
            "{'some-json': 'body'}"
        }
        header { "Authorization: Bearer some_token" }
    }
```


### TODO:
- Documentation Page
- Header Types
- Builder Factory

---

:hammer: **This is an early version and is still a WIP!** :hammer: