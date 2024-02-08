Cliente en Android que utiliza certificado, certificado creado así:

```Shell
keytool -genkey -alias servidor  -keyalg RSA -keystore AlmacenSrv -storepass 1234567
keytool -exportcert -alias servidor -keystore AlmacenSrv -storepass 1234567 -file my_certificate.crt
```

Certificado almacenado en la carpeta res -> raw
