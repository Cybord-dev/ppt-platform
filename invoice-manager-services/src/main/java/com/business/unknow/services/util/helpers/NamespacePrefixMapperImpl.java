package com.business.unknow.services.util.helpers;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
import java.util.Map;

public final class NamespacePrefixMapperImpl extends NamespacePrefixMapper {

  private final Map<String, String> map;

  public NamespacePrefixMapperImpl(Map<String, String> map) {
    this.map = map;
  }

  public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
    String value = map.get(namespaceUri);
    return (value != null) ? value : suggestion;
  }

  @Override
  public String[] getPreDeclaredNamespaceUris() {
    return new String[0];
  }
}
