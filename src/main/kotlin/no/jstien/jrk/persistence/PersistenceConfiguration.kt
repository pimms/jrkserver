package no.jstien.jrk.persistence

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.gson.GsonBuilder
import com.ning.http.client.AsyncHttpClient
import org.restonfire.BaseFirebaseRestDatabaseFactory
import org.restonfire.FirebaseRestDatabase
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

@Configuration
open class PersistenceConfiguration {
    @Value("\${firebase.url}") private val firebaseUrl: String? = null
    @Value("\${firebase.keyFile}") private val keyFile: String? = null

    @Bean
    open fun firebaseAccessToken(): String {
        val keyStream = javaClass.classLoader.getResourceAsStream(keyFile)
        val googleCred = GoogleCredential.fromStream(keyStream)
        val scoped = googleCred.createScoped(
            Arrays.asList(
                "https://www.googleapis.com/auth/firebase.database",
                "https://www.googleapis.com/auth/userinfo.email"
            )
        )

        scoped.refreshToken()
        return scoped.accessToken
    }

    @Bean
    open fun firebaseDatabase(): FirebaseRestDatabase {
        val factory = BaseFirebaseRestDatabaseFactory(AsyncHttpClient(), GsonBuilder().create())
        val database = factory.create("https://jrkapp-fa135.firebaseio.com/", "nMu9trIXuZ1r1CSRcF10z9cQguUgbt2eEDCpUXTT")
        return database
    }

    @Bean
    @Autowired
    open fun persistentEpisodeRepository(
            firebaseDatabase: FirebaseRestDatabase,
            accessToken: String
    ): PersistentEpisodeRepository {
        return PersistentEpisodeRepository(firebaseDatabase)
    }
}