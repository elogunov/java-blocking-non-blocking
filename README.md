
_The module contains show case classes for blocking/non-blocking concurrency algorithms:_
* `syncronized` resource access. Provides blocking access with ability to wait for the next value.
* resource access using `AtomicReference`. Provides non-blocking access, but without ability to wait for the new value
* resource access using `AbstractQueuedSynchronizer`. Provides non-blocking access with ability to wait for the next value
