package org.edge.protocol.opcua.api.server;

import org.edge.protocol.opcua.api.common.EdgeNodeIdentifier;

public class EdgeReference {
  private String sourcePath;
  private String sourceNamespace;
  private String targetPath;
  private String targetNamespace;
  private EdgeNodeIdentifier referenceId = EdgeNodeIdentifier.Organizes;
  private boolean forward = true;

  public static class Builder {
    private final String sourcePath;
    private final String sourceNamespace;
    private final String targetPath;
    private final String targetNamespace;
    private EdgeNodeIdentifier referenceId = EdgeNodeIdentifier.Organizes;
    private boolean forward = true;

    public Builder(String sourcePath, String sourceNamespace, String targetPath,
        String targetNamespace) {
      this.sourcePath = sourcePath;
      this.sourceNamespace = sourceNamespace;
      this.targetPath = targetPath;
      this.targetNamespace = targetNamespace;
    }

    public Builder setReferenceId(EdgeNodeIdentifier referenceId) {
      this.referenceId = referenceId;
      return this;
    }

    public Builder setForward(boolean forward) {
      this.forward = forward;
      return this;
    }

    public EdgeReference build() {
      return new EdgeReference(this);
    }
  }

  private EdgeReference(Builder builder) {
    sourcePath = builder.sourcePath;
    sourceNamespace = builder.sourceNamespace;
    targetPath = builder.targetPath;
    targetNamespace = builder.targetNamespace;
    referenceId = builder.referenceId;
    forward = builder.forward;
  }

  public String getSourcePath() {
    return sourcePath;
  }

  public String getSourceNamespace() {
    return sourceNamespace;
  }

  public String getTargetPath() {
    return targetPath;
  }

  public String getTargetNamespace() {
    return targetNamespace;
  }

  public EdgeNodeIdentifier getReference() {
    return referenceId;
  }

  public boolean getForward() {
    return forward;
  }
}
