/*
 * Copyright (c) 2016 Kevin Herron
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v1.0 which accompany
 * this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html and the
 * Eclipse Distribution License is available at http://www.eclipse.org/org/documents/edl-v10.html.
 */

package org.edge.protocol.opcua.session.auth;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

public class KeyStoreLoader {

  private static final String CLIENT_ALIAS = "client-ai";
  private static final String SERVER_ALIAS = "server-ai";
  private static final char[] PASSWORD = "password".toCharArray();

  private X509Certificate clientCertificate;
  private KeyPair clientKeyPair;
  private X509Certificate serverCertificate;
  private KeyPair serverKeyPair;

  /**
   * @fn load()
   * @brief load key store
   * @param [in] void
   * @return KeyStoreLoader
   */
  public KeyStoreLoader load() throws Exception {
    KeyStore keyStore = KeyStore.getInstance("PKCS12");
    keyStore.load(getClass().getClassLoader().getResourceAsStream("example-certs.pfx"), PASSWORD);

    Key clientPrivateKey = keyStore.getKey(CLIENT_ALIAS, PASSWORD);
    if (clientPrivateKey instanceof PrivateKey) {
      clientCertificate = (X509Certificate) keyStore.getCertificate(CLIENT_ALIAS);
      PublicKey clientPublicKey = clientCertificate.getPublicKey();
      clientKeyPair = new KeyPair(clientPublicKey, (PrivateKey) clientPrivateKey);
    }

    Key serverPrivateKey = keyStore.getKey(SERVER_ALIAS, PASSWORD);
    if (serverPrivateKey instanceof PrivateKey) {
      serverCertificate = (X509Certificate) keyStore.getCertificate(SERVER_ALIAS);
      PublicKey serverPublicKey = serverCertificate.getPublicKey();
      serverKeyPair = new KeyPair(serverPublicKey, (PrivateKey) serverPrivateKey);
    }

    return this;
  }

  /**
   * @fn getClientCertificate()
   * @brief get client certificate
   * @param [in] void
   * @return X509Certificate
   */
  public X509Certificate getClientCertificate() {
    return clientCertificate;
  }

  /**
   * @fn getClientKeyPair()
   * @brief get client pair
   * @param [in] void
   * @return KeyPair
   */
  public KeyPair getClientKeyPair() {
    return clientKeyPair;
  }

  /**
   * @fn getServerCertificate()
   * @brief get server pair
   * @param [in] void
   * @return X509Certificate
   */
  public X509Certificate getServerCertificate() {
    return serverCertificate;
  }

  /**
   * @fn getServerKeyPair()
   * @brief get server pair
   * @param [in] void
   * @return KeyPair
   */
  public KeyPair getServerKeyPair() {
    return serverKeyPair;
  }

}
