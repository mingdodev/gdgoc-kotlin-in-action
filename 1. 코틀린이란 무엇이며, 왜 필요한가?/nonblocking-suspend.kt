    suspend fun processUser(credentials: Credentials) {
        val user = authenticate(credentials)
        val data = loadUserData(user)
        val profilePicture = loadImage(data.imageID)
    }
    
    suspend fun authenticate(c: Credentials): User { /* .. */ }
    suspend fun loadUserData(u: User): Data { /* .. */ }
    suspend fun loadImage(id: Int): Image { /* .. */ }