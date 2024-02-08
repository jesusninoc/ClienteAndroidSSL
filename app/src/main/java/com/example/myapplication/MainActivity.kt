package com.example.myapplication

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.theme.MyApplicationTheme
import java.io.DataInputStream
import java.io.DataOutputStream
import java.security.KeyStore
import java.security.cert.CertificateFactory
import javax.net.ssl.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Cliente")
                }
            }
        }

        // Iniciar cliente con el contexto de la aplicación
        val cliente = Cliente(this)
        cliente.execute()
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "$name!",
        modifier = modifier
    )
}

class Cliente(private val context: Context) : AsyncTask<Void, Void, Void>() {
    override fun doInBackground(vararg params: Void?): Void? {
        val host = "192.168.1.55"
        val puerto = 5556
        val keyStorePassword = "1234567"
        val keyPassword = "1234567"

        // Cargar el certificado desde res/raw
        val certificate = context.resources.openRawResource(R.raw.my_certificate)
        val certificateFactory = CertificateFactory.getInstance("X.509")
        val cert = certificateFactory.generateCertificate(certificate)

        // Configurar el almacén de claves
        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
        keyStore.load(null, null)
        keyStore.setCertificateEntry("certificate", cert)

        // Configurar el administrador de claves
        val keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
        keyManagerFactory.init(keyStore, keyPassword.toCharArray())

        // Configurar el administrador de confianza (usamos el mismo almacén de claves para simplificar)
        val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(keyStore)

        // Configurar el contexto SSL
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(keyManagerFactory.keyManagers, trustManagerFactory.trustManagers, null)

        // Crear el socket SSL del cliente
        val sslSocketFactory = sslContext.socketFactory
        val cliente = sslSocketFactory.createSocket(host, puerto) as SSLSocket

        // Creo flujo de salida al servidor
        val flujoSalida = DataOutputStream(cliente.getOutputStream())

        // Envío un saludo al servidor
        flujoSalida.writeUTF("Saludos al SERVIDOR DESDE EL CLIENTE")

        // Creo flujo de entrada al servidor
        val flujoEntrada = DataInputStream(cliente.getInputStream())

        // El servidor me envía un mensaje
        println("Recibiendo del SERVIDOR: \n\t${flujoEntrada.readUTF()}")

        // Cerrar streams y sockets
        flujoEntrada.close()
        flujoSalida.close()
        cliente.close()

        return null
    }
}
