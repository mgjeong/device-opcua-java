/******************************************************************
 *
 * Copyright 2017 Samsung Electronics All Rights Reserved.
 *
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************/

package org.edge.protocol.opcua.example;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.ubyte;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.ulong;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.ushort;
import java.util.UUID;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.ByteString;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.XmlElement;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UByte;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.ULong;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UShort;

public enum EdgeSampleCommon {
  SERVER_NODE_LONG_PATH(1, "/AA/BB/CC/DD/EE/FF/GG/HH/II/", ""),
  SERVER_NODE_LINE1(10, "/Line/1/", ""), 
  SERVER_NODE_LINE2(11, "Line_2", ""), 
  SERVER_NODE_ROBOT(12, "Robot", ""), 
  SERVER_NODE_GLASS(13, "samsung/3d-glass", ""), 
  SERVER_NODE_LINE7(14, "Line_7", ""),
  SERVER_NODE_WRITE_ONLY(15, "Line_3", ""), 
  SERVER_NODE_READ_ONLY(16, "Line_4",""), 
  SERVER_NODE_READ_NOT_USER(17, "Line_5", ""), 
  SERVER_NODE_WRITE_NOT_USER(18, "Line_6", ""),
  SERVER_NODE_ARRAY(19, "Array", ""),
  VIEW_NAME(52, "Line1_view", ""),
  METHOD_ABS(48, "abs(x)", ""),
  METHOD_SQRT(50, "sqrt(x)", ""),
  VARIABLE_TYPE_NAME(53, "variable type", ""), 
  OBJECT_TYPE_NAME(54, "object type", ""),
  REFERENCE_TYPE_NAME(55, "reference type", ""), 
  DATA_TYPE_NAME(56, "data type", ""),
  OBJECT_NAME(57, "object", ""),
  TARGET_NODE_LINE3_UINT(34, "Line_3UInt64", ""),
  TARGET_NODE_LINE4_UINT(35, "Line_4UInt64", ""),
  TARGET_NODE_LINE5_UINT(36, "Line_5UInt64", ""),
  
  KEY_URI_LINE_CNC14(20, "/defaultRootNode/Line/1/cnc14", ""),
  KEY_URI_LINE_TEMPORATURE(21, "/defaultRootNode/Line/1/Temperature", ""),
  KEY_URI_DA_TEMPORATURE1(22, "/defaultRootNode/Location/AnalogItemType", ""),
  KEY_URI_DA_CAM(23, "/defaultRootNode/Location/ImageItemType", ""),
  KEY_URI_LINE_CNC100(24, "/defaultRootNode/Line/1/cnc100", ""),
  TARGET_NODE_CNC14(29, "/Line/1/cnc14", ""),
  TARGET_NODE_CNC100(30, "/Line/1/cnc100", ""),
  TARGET_NODE_ROBOT(31, "RobotLocation", ""), 
  TARGET_NODE_TEMPORATURE(32, "/Line/1/Temperature", ""), 
  TARGET_NODE_GLASS(33, "/samsung/3d-glass/Byte", ""),
  TARGET_LINE_VIEW_NODE(34, "/Line1_view", ""),
  
  KEY_URI_METHOD_SQRT(51, "/defaultRootNode/Line_7/sqrt(x)", "");
   

  private int code;
  private String value;
  private String description;
  static Boolean[] arrayOfBool = {Boolean.valueOf(true), Boolean.valueOf(false),
      Boolean.valueOf(true), Boolean.valueOf(true), Boolean.valueOf(false)};
  static UByte[] arrayOfByte = {UByte.valueOf(0x00), UByte.valueOf(0x01), UByte.valueOf(0x03),
      UByte.valueOf(0x02), UByte.valueOf(0x03)};
  static Integer[] arrayOfInt = {1, 1, 2, 3, 4, 5, 6, 7};
  static Double[] arrayOfDouble = {Double.valueOf(1), Double.valueOf(2), Double.valueOf(3),
      Double.valueOf(4), Double.valueOf(5)};
  static Float[] arrayOfFloat =
      {Float.valueOf(1), Float.valueOf(2), Float.valueOf(3), Float.valueOf(4), Float.valueOf(5)};
  static ByteString[] arrayOfByteString = {new ByteString(new byte[] {0x01, 0x02, 0x03, 0x04, 0x01}),
      new ByteString(new byte[] {0x01, 0x02, 0x03, 0x04, 0x02}), 
      new ByteString(new byte[] {0x01, 0x02, 0x03, 0x04, 0x03}),
      new ByteString(new byte[] {0x01, 0x02, 0x03, 0x04, 0x04}), 
      new ByteString(new byte[] {0x01, 0x02, 0x03, 0x04, 0x05})};
  static String[] arrayOfString = {"String_1", "String_2", "String_3", "String_4", "String_5"};
  static DateTime[] arrayPfDateTime = {DateTime.now(), DateTime.now(), DateTime.now(), DateTime.now(), DateTime.now()};
  static UUID[] arrayOfGuid = {UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()};
  static Short[] arrayOfInt16 = {(short)16, (short)16, (short)16, (short)16, (short)16};
  static Long[] arrayOfInt64 = {(long)64, (long)64, (long)64, (long)64, (long)64};
  static LocalizedText[] arrayOfLocalizedText = {new LocalizedText("en1", "localized text1"), new LocalizedText("en2", "localized text2"),
      new LocalizedText("en3", "localized text3"), new LocalizedText("en4", "localized text4"), new LocalizedText("en5", "localized text5")};
  static QualifiedName[] arrayOfQualifiedName = {new QualifiedName(12341, "defg1"), new QualifiedName(12342, "defg2"), new QualifiedName(12343, "defg3"),
      new QualifiedName(12344, "defg4"), new QualifiedName(12345, "defg5")};
  static Byte[] arrayOfSBbyte = {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};  
  static UShort[] arrayOfUInt16 = {ushort(16), ushort(16), ushort(16), ushort(16) ,ushort(16)};
  static UInteger[] arrayOfUInt32 = {uint(32), uint(32), uint(32), uint(32), uint(32)};
  static ULong[] arrayOfUint64 = {ulong(64L), ulong(64L), ulong(64L), ulong(64L), ulong(64L)};
  static XmlElement[] arrayOfXmlElement = {new XmlElement("<a>hello1</a>"), new XmlElement("<a>hello2</a>"),
      new XmlElement("<a>hello3</a>"), new XmlElement("<a>hello4</a>"), new XmlElement("<a>hello5</a>")};
  
  
  
  public static final Object[][] GLASS_VARIABLE_NODES = new Object[][] {
      {"Bool", Identifiers.Boolean, new Variant(false)},
      {"Byte", Identifiers.Byte, new Variant(ubyte(0x00))},
      {"ByteString", Identifiers.ByteString,
          new Variant(new ByteString(new byte[] {0x01, 0x02, 0x03, 0x04}))},
      {"DateTime", Identifiers.DateTime, new Variant(DateTime.now())},
      {"Double", Identifiers.Double, new Variant(3.14d)},
      {"Duration", Identifiers.Duration, new Variant(1.2d)},
      {"Float", Identifiers.Float, new Variant(3.14f)},
      {"Guid", Identifiers.Guid, new Variant(UUID.randomUUID())},
      {"Int16", Identifiers.Int16, new Variant((short) 16)},
      {"Int32", Identifiers.Int32, new Variant(Integer.valueOf(32))}, 
      {"Int64", Identifiers.Int64, new Variant(64L)},
      {"LocalizedText", Identifiers.LocalizedText,
          new Variant(new LocalizedText("en", "localized text"))},
      {"Integer", Identifiers.Integer, new Variant(Integer.valueOf(10))},
      {"UInteger", Identifiers.UInteger, new Variant(UInteger.valueOf(10))},
      {"NodeId", Identifiers.NodeId, new Variant(new NodeId(1234, "abcd"))},
      {"QualifiedName", Identifiers.QualifiedName, new Variant(new QualifiedName(1234, "defg"))},
      {"SByte", Identifiers.SByte, new Variant((byte) 0x00)},
      {"String", Identifiers.String, new Variant("string value")},
      {"UtcTime", Identifiers.UtcTime, new Variant(DateTime.now())},
      {"UInt16", Identifiers.UInt16, new Variant(ushort(16))},
      {"UInt32", Identifiers.UInt32, new Variant(uint(32))},
      {"UInt64", Identifiers.UInt64, new Variant(ulong(64L))},
      {"XmlElement", Identifiers.XmlElement, new Variant(new XmlElement("<a>hello</a>"))},};

  public static final Object[][] WRITE_ONLY_NODES =
      new Object[][] {{"UInt64", Identifiers.UInt64, new Variant(ulong(64L))}};

  public static final Object[][] READ_ONLY_NODES =
      new Object[][] {{"UInt64", Identifiers.UInt64, new Variant(ulong(64L))}, {"LocalizedText",
          Identifiers.LocalizedText, new Variant(new LocalizedText("en", "localized text"))}};

  public static final Object[][] LINE_NODES =
      new Object[][] {{"cnc14", Identifiers.String, new Variant("on")},
          {"Temperature", Identifiers.UInt32, new Variant(uint(720))},
          {"cnc100", Identifiers.UInt32, new Variant(uint(80))}};

  public static final Object[][] ARRAY_NODES =
      new Object[][] {{"Int32", Identifiers.Int32, new Variant(arrayOfInt)},
          {"Boolean", Identifiers.Boolean, new Variant(arrayOfBool)},
          {"Byte", Identifiers.Byte, new Variant(arrayOfByte)},
          {"Dobule", Identifiers.Double, new Variant(arrayOfDouble)},
          {"Float", Identifiers.Float, new Variant(arrayOfFloat)},
          {"String", Identifiers.String, new Variant(arrayOfString)},
          {"ByteString", Identifiers.ByteString, new Variant(arrayOfByteString)},       
          {"DateTime", Identifiers.DateTime, new Variant(arrayPfDateTime)},
          {"Guid", Identifiers.Guid, new Variant(arrayOfGuid)},
          {"Int16", Identifiers.Int16, new Variant(arrayOfInt16)},
          {"Int64", Identifiers.Int64, new Variant(arrayOfInt64)},
          {"LocalizedText", Identifiers.LocalizedText, new Variant(arrayOfLocalizedText)},
          {"QualifiedName", Identifiers.QualifiedName, new Variant(arrayOfQualifiedName)},
          {"SByte", Identifiers.SByte, new Variant(arrayOfSBbyte)},
          {"Uint16", Identifiers.UInt16, new Variant(arrayOfUInt16)},
          {"Uint32", Identifiers.UInt32, new Variant(arrayOfUInt32)},
          {"Uint64", Identifiers.UInt64, new Variant(arrayOfUint64)},
          {"XmlElement", Identifiers.XmlElement, new Variant(arrayOfXmlElement)}};

  public static final Object[][] VARIABLE_TYPE_NODE =
      new Object[][] {{"UInt64", Identifiers.UInt64, new Variant(ulong(64L))}};

  public static final Object[][] ROBOT_NODES =
      new Object[][] {{"Location", Identifiers.String, new Variant("100")}};

  public static final Object[][] BASIC_TYPES = new Object[][] {
      {"Double", Identifiers.Double, new Variant(3.14d)},
      {"Float", Identifiers.Float, new Variant(3.14f)},
      {"Int16", Identifiers.Int16, new Variant((short) 16)},
      {"Int32", Identifiers.Int32, new Variant(32)}, {"Int64", Identifiers.Int64, new Variant(64L)},
      {"UInt16", Identifiers.UInt16, new Variant(ushort(16))},
      {"UInt32", Identifiers.UInt32, new Variant(uint(32))},
      {"UInt64", Identifiers.UInt64, new Variant(ulong(64L))}};
            

  private EdgeSampleCommon(int code, String value, String description) {
    this.code = code;
    this.value = value;
    this.description = description;
  }

  public int getCode() {
    return code;
  }

  public String getValue() {
    return value;
  }

  public String getDescription() {
    return description;
  }
}
