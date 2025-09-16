suspend fun loadAndOverlay(first: String, second: String): Image = 
        coroutineScope {
            val firstDeferred = async { loadImage(first) }
            val secondDeferred = async { loadImage(second) }
            combineImages(firstDeferred.await(), secondDeferred.await())
        }