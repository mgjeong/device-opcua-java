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

import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.util.Optional;
import java.util.Set;
import org.eclipse.milo.opcua.stack.core.application.CertificateManager;
import org.eclipse.milo.opcua.stack.core.types.builtin.ByteString;
import com.google.common.collect.Sets;

public class TestCertificateManager implements CertificateManager {

  private final KeyPair keyPair;
  private final X509Certificate certificate;

  /**
   * @fn TestCertificateManager(KeyPair keyPair, X509Certificate certificate)
   * @brief Constructor of TestCertificateManager class
   */
  public TestCertificateManager(KeyPair keyPair, X509Certificate certificate) {
    this.keyPair = keyPair;
    this.certificate = certificate;
  }
  
  /**
   * @brief overriding methods from CertificateManager
   */
  @Override
  public Optional<KeyPair> getKeyPair(ByteString thumbprint) {
    return Optional.of(keyPair);
  }

  @Override
  public Optional<X509Certificate> getCertificate(ByteString thumbprint) {
    return Optional.of(certificate);
  }

  @Override
  public Set<KeyPair> getKeyPairs() {
    return Sets.newHashSet(keyPair);
  }

  @Override
  public Set<X509Certificate> getCertificates() {
    return Sets.newHashSet(certificate);
  }

}
