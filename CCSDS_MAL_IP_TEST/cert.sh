#!/usr/bin/env bash

# ip=$(ifconfig | grep -A 1 'en0'| tail -1 | cut -d' ' -f 2)
ip="127.0.0.1"
filename="store.pass"
dname="CN=ccsds.demo.server,OU=MyGrp,O=JPL,L=LA,ST=CA,C=US"
dname1="CN=ccsds.demo.client,OU=MyGrp,O=JPL,L=LA,ST=CA,C=US"

function gen
{
    pwd
    rm -f server.jks server.cert client1.jks client1.cert client2.jks client2.cert client3.jks client3.cert server.pfx server-trust.pfx client1.pfx client1-trust.pfx server-trust.jks client1-trust.jks
    keytool -genkey -keyalg RSA -keysize 2048 -alias server -keystore server.jks -keypass:file $filename -storepass:file $filename -validity 360 -ext san=IP:$ip -dname $dname
    keytool -genkey -keyalg RSA -keysize 2048 -alias client1 -keystore client1.jks -keypass:file $filename -storepass:file $filename -validity 360 -ext san=IP:$ip -dname $dname

    keytool -export -file server.cert -keystore server.jks -alias server -storepass:file $filename
    keytool -export -file client1.cert -keystore client1.jks -alias client1 -storepass:file $filename

    echo yes | keytool -import -file server.cert -keystore client1-trust.jks -alias server -storepass:file $filename

    echo yes | keytool -import -file client1.cert -keystore server-trust.jks -alias client1 -storepass:file $filename

    keytool -importkeystore -srckeystore server.jks -srcalias server -srcstoretype JKS -srcstorepass:file $filename -destkeystore server.pfx -destalias server -deststoretype PKCS12 -deststorepass:file $filename
    keytool -importkeystore -srckeystore server-trust.jks -srcalias client1 -srcstoretype JKS -srcstorepass:file $filename -destkeystore server-trust.pfx -destalias client1 -deststoretype PKCS12 -deststorepass:file $filename
    keytool -importkeystore -srckeystore client1.jks -srcalias client1 -srcstoretype JKS -srcstorepass:file $filename -destkeystore client1.pfx -destalias client1 -deststoretype PKCS12 -deststorepass:file $filename
    keytool -importkeystore -srckeystore client1-trust.jks -srcalias server -srcstoretype JKS -srcstorepass:file $filename -destkeystore client1-trust.pfx -destalias server -deststoretype PKCS12 -deststorepass:file $filename
}

function print
{
    echo "Current IP = "$ip
    echo "~~~~~~~~~~~~~~~~~~~~~~~~~~"
    echo "Printing all Key Store Files"
    echo "============================"
    echo "Server"
    echo "------"
    keytool -list -keystore server.jks -storepass:file $filename
    echo "~~~~~~~~~~~~~~~~~~~~~~~~~~"
    echo "Client1"
    echo "------"
    keytool -list -keystore client1.jks -storepass:file $filename
    echo "~~~~~~~~~~~~~~~~~~~~~~~~~~"
    echo "Server-Trust"
    echo "------"
    keytool -list -keystore server-trust.jks -storepass:file $filename
    echo "~~~~~~~~~~~~~~~~~~~~~~~~~~"
    echo "Client1-Trust"
    echo "------"
    keytool -list -keystore client1-trust.jks -storepass:file $filename
    echo "~~~~~~~~~~~~~~~~~~~~~~~~~~"
}
echo $ip
gen
print
