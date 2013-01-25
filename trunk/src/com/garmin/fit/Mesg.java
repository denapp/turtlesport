////////////////////////////////////////////////////////////////////////////////
// The following FIT Protocol software provided may be used with FIT protocol
// devices only and remains the copyrighted property of Dynastream Innovations Inc.
// The software is being provided on an "as-is" basis and as an accommodation,
// and therefore all warranties, representations, or guarantees of any kind
// (whether express, implied or statutory) including, without limitation,
// warranties of merchantability, non-infringement, or fitness for a particular
// purpose, are specifically disclaimed.
//
// Copyright 2013 Dynastream Innovations Inc.
////////////////////////////////////////////////////////////////////////////////
// ****WARNING****  This file is auto-generated!  Do NOT edit this file.
// Profile Version = 5.20Release
// Tag = $Name: AKW5_200 $
////////////////////////////////////////////////////////////////////////////////


package com.garmin.fit;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Mesg {
   protected String name;
   protected int num;
   protected int localNum;
   protected ArrayList<Field> fields;
   protected long systemTimeOffset;

   public Mesg(final Mesg mesg) {
      this.fields = new ArrayList<Field>();

      if (mesg == null) {
         this.name = "unknown";
         this.num = MesgNum.INVALID;
         this.systemTimeOffset = 0;
         return;
      }

      this.name = mesg.name;
      this.num = mesg.num;
      this.localNum = mesg.localNum;
      this.systemTimeOffset = mesg.systemTimeOffset;
      for (Field field : mesg.fields) {
         if (field.getNumValues() > 0) {
            this.fields.add(new Field(field));
         }
      }
   }

   protected Mesg(String name, int num) {
      this.name = new String(name);
      this.num = num;
      this.localNum = 0;
      this.fields = new ArrayList<Field>();
      this.systemTimeOffset = 0;
   }

   public void write(OutputStream out) {
      write(out, null);
   }

   public void write(OutputStream out, MesgDefinition mesgDef) {
      try {
         new DataOutputStream(out).writeByte(localNum & Fit.HDR_TYPE_MASK); // Message record header.
      } catch (java.io.IOException e) {
      }

      if (mesgDef == null)
         mesgDef = new MesgDefinition(this);

      for (FieldDefinition fieldDef : mesgDef.fields) {
         Field field = this.getField(fieldDef.num);

         if (field == null)
            field = Factory.createField(num, fieldDef.num);
            
         field.write(out, fieldDef);
      }
   }

   public String getName() {
      return name;
   }

   public int getNum() {
      return num;
   }

   public boolean hasField(int num) {
      for (int i = 0; i < fields.size(); i++) {
         if (fields.get(i).num == num)
            return true;
      }

      return false;
   }

   public void addField(Field field) {
      fields.add(field);
   }

   public void setField(Field field) {
      for (int i = 0; i < fields.size(); i++) {
         if (fields.get(i).num == field.num) {
            fields.set(i, field);
            return;
         }
      }

      fields.add(field);
   }

   public void setFields(Mesg mesg) {
      if (mesg.num != num)
         return;
      
      for (Field field : mesg.fields) {
         setField(field);
      }
   }

   public int getNumFields() {
      return fields.size();
   }

   public Field getField(int num) {
      for (int i = 0; i < fields.size(); i++) {
         if (fields.get(i).num == num)
            return fields.get(i);
      }

      return null;
   }

   public Field getField(String name) {
      return getField(name, true);
   }

   public Field getField(String name, boolean checkMesgSupportForSubFields) {
      for (int i = 0; i < fields.size(); i++) {
         if (fields.get(i).name.equals(name))
            return fields.get(i);

         for (int j = 0; j < fields.get(i).subFields.size(); j++) {
            if ((fields.get(i).subFields.get(j).name.equals(name)) && (!checkMesgSupportForSubFields || (fields.get(i).subFields.get(j).canMesgSupport(this))))
               return fields.get(i);
         }
      }

      return null;
   }

   public int GetActiveSubFieldIndex(int num) {
      final Field testField = Factory.createField(this.num, num);

      if (testField == null)
         return Fit.SUBFIELD_INDEX_MAIN_FIELD;

      for (int i = 0; i < testField.subFields.size(); i++) {
         if (testField.subFields.get(i).canMesgSupport(this))
            return i;
      }

      return Fit.SUBFIELD_INDEX_MAIN_FIELD;
   }

   public String GetActiveSubFieldName(int num) {
      final Field testField = Factory.createField(this.num, num);

      if (testField == null)
         return Fit.SUBFIELD_NAME_MAIN_FIELD;

      for (int i = 0; i < testField.subFields.size(); i++) {
         if (testField.subFields.get(i).canMesgSupport(this))
            return testField.subFields.get(i).getName();
      }

      return Fit.SUBFIELD_NAME_MAIN_FIELD;
   }

   public int getNumFieldValues(int num) {
      return getNumFieldValues(num, Fit.SUBFIELD_INDEX_ACTIVE_SUBFIELD);
   }

   public int getNumFieldValues(int num, int subFieldIndex) {
      final Field field = getField(num);

      if (field == null)
         return 0;

      if (subFieldIndex == Fit.SUBFIELD_INDEX_ACTIVE_SUBFIELD)
         return field.getNumValues();

      final SubField subField = field.getSubField(subFieldIndex);

      if ((subField == null) || (subField.canMesgSupport(this)))
         return field.getNumValues();
      else
         return 0;
   }

   public int getNumFieldValues(int num, String subFieldName) {
      final Field field = getField(num);

      if (field == null)
         return 0;

      final SubField subField = field.getSubField(subFieldName);

      if ((subField == null) || (subField.canMesgSupport(this)))
         return field.getNumValues();
      else
         return 0;
   }

   public int getNumFieldValues(String name) {
      final Field field = getField(name, false);

      if (field == null)
         return 0;
      
      final SubField subField = field.getSubField(name);
      
      if ((subField == null) || (subField.canMesgSupport(this)))
         return field.getNumValues();
      else
         return 0;
   }

   public Object getFieldValue(int num) {
      return getFieldValue(num, 0, Fit.SUBFIELD_INDEX_ACTIVE_SUBFIELD);
   }

   public Object getFieldValue(int num, int fieldArrayIndex) {
      return getFieldValue(num, fieldArrayIndex, Fit.SUBFIELD_INDEX_ACTIVE_SUBFIELD);
   }

   public Object getFieldValue(int num, int fieldArrayIndex, int subFieldIndex) {
      final Field field = getField(num);

      if (field == null)
         return null;

      if (subFieldIndex == Fit.SUBFIELD_INDEX_ACTIVE_SUBFIELD) {
         return field.getValue(fieldArrayIndex, GetActiveSubFieldIndex(num));
      }
      else {
         final SubField subField = field.getSubField(subFieldIndex);
      
         if ((subField == null) || (subField.canMesgSupport(this)))
            return field.getValue(fieldArrayIndex, subFieldIndex);
         else
            return null;
      }
   }

   public Object getFieldValue(int num, int fieldArrayIndex, String subFieldName) {
      final Field field = getField(num);

      if (field == null)
         return null;

      final SubField subField = field.getSubField(subFieldName);

      if ((subField == null) || (subField.canMesgSupport(this)))
         return field.getValue(fieldArrayIndex, subFieldName);
      else
         return null;
   }

   public Object getFieldValue(String name) {
      return getFieldValue(name, 0);
   }

   public Object getFieldValue(String name, int fieldArrayIndex) {
      final Field field = getField(name, false);

      if (field == null)
         return null;

      final SubField subField = field.getSubField(name);

      if ((subField == null) || (subField.canMesgSupport(this)))
         return field.getValue(fieldArrayIndex, name);
      else
         return null;
   }
   
   public void setFieldValue(int num, Object value) {
      setFieldValue(num, 0, value, Fit.SUBFIELD_INDEX_ACTIVE_SUBFIELD);
   }

   public void setFieldValue(int num, int fieldArrayIndex, Object value) {
      setFieldValue(num, fieldArrayIndex, value, Fit.SUBFIELD_INDEX_ACTIVE_SUBFIELD);
   }

   public void setFieldValue(int num, int fieldArrayIndex, Object value, int subFieldIndex) {
      if (subFieldIndex == Fit.SUBFIELD_INDEX_ACTIVE_SUBFIELD) {
         subFieldIndex = GetActiveSubFieldIndex(num);
      }
      else {
         final Field testField = Factory.createField(this.num, num);
         final SubField subField = testField.getSubField(subFieldIndex);

         if ((subField != null) && !(subField.canMesgSupport(this)))
            return;
      }

      Field field = getField(num);

      if (field == null) {
         field = Factory.createField(this.num, num);
         addField(field);
      }

      field.setValue(fieldArrayIndex, value, subFieldIndex);
   }

   public void setFieldValue(int num, int fieldArrayIndex, Object value, String subFieldName) {
      final Field testField = Factory.createField(this.num, num); 
      final SubField subField = testField.getSubField(subFieldName);

      if ((subField != null) && !(subField.canMesgSupport(this)))
         return;
      
      Field field = getField(num);

      if (field == null) {
         field = Factory.createField(this.num, num);
         addField(field);
      }

      field.setValue(fieldArrayIndex, value, subFieldName);
   }

   public void setFieldValue(String name, Object value) {
      setFieldValue(name, 0, value);
   }

   public void setFieldValue(String name, int fieldArrayIndex, Object value) {
      final Field testField = Factory.createField(this.num, name); 
      final SubField subField = testField.getSubField(name);

      if ((subField != null) && !(subField.canMesgSupport(this)))
         return;

      Field field = getField(name, false);

      if (field == null) {
         field = Factory.createField(this.num, name);
         addField(field);
      }

      field.setValue(fieldArrayIndex, value, name);
   }

   public Long getFieldBitsValue(int num, int offset, int bits, boolean signed) {
      final Field field = getField(num);

      if (field == null)
         return null;

      return field.getBitsValue(offset, bits, signed);
   }

   public Long getFieldBitsValue(String name, int offset, int bits, boolean signed) {
      final Field field = getField(name, false);

      if (field == null)
         return null;
      
      final SubField subField = field.getSubField(name);

      if ((subField == null) || (subField.canMesgSupport(this)))      
         return field.getBitsValue(offset, bits, signed);
      else
         return null;
   }

   public Byte getFieldByteValue(int num) {
      return getFieldByteValue(num, 0, Fit.SUBFIELD_INDEX_ACTIVE_SUBFIELD);
   }

   public Byte getFieldByteValue(int num, int fieldArrayIndex) {
      return getFieldByteValue(num, fieldArrayIndex, Fit.SUBFIELD_INDEX_ACTIVE_SUBFIELD);
   }
   
   public Byte getFieldByteValue(int num, int fieldArrayIndex, int subFieldIndex) {
      final Field field = getField(num);

      if (field == null)
         return null;

      if (subFieldIndex == Fit.SUBFIELD_INDEX_ACTIVE_SUBFIELD) {
         return field.getByteValue(fieldArrayIndex, GetActiveSubFieldIndex(num));
      }
      else {
         final SubField subField = field.getSubField(subFieldIndex);

         if ((subField == null) || (subField.canMesgSupport(this)))
            return field.getByteValue(fieldArrayIndex, subFieldIndex);
         else
            return null;         
      }
   }

   public Byte getFieldByteValue(int num, int fieldArrayIndex, String subFieldName) {
      final Field field = getField(num);

      if (field == null)
         return null;

      final SubField subField = field.getSubField(subFieldName);

      if ((subField == null) || (subField.canMesgSupport(this)))
         return field.getByteValue(fieldArrayIndex, subFieldName);
      else
         return null;
   }

   public Byte getFieldByteValue(String name) {
      return getFieldByteValue(name, 0);
   }

   public Byte getFieldByteValue(String name, int fieldArrayIndex) {
      final Field field = getField(name, false);

      if (field == null)
         return null;

      final SubField subField = field.getSubField(name);

      if ((subField == null) || (subField.canMesgSupport(this)))
         return field.getByteValue(fieldArrayIndex, name);
      else
         return null;
   }

   public Short getFieldShortValue(int num) {
      return getFieldShortValue(num, 0, Fit.SUBFIELD_INDEX_ACTIVE_SUBFIELD);
   }

   public Short getFieldShortValue(int num, int fieldArrayIndex) {
      return getFieldShortValue(num, fieldArrayIndex, Fit.SUBFIELD_INDEX_ACTIVE_SUBFIELD);
   }

   public Short getFieldShortValue(int num, int fieldArrayIndex, int subFieldIndex) {
      final Field field = getField(num);

      if (field == null)
         return null;

      if (subFieldIndex == Fit.SUBFIELD_INDEX_ACTIVE_SUBFIELD) {
         return field.getShortValue(fieldArrayIndex, GetActiveSubFieldIndex(num));
      }
      else {
         final SubField subField = field.getSubField(subFieldIndex);
   
         if ((subField == null) || (subField.canMesgSupport(this)))
            return field.getShortValue(fieldArrayIndex, subFieldIndex);
         else
            return null;
      }
   }

   public Short getFieldShortValue(int num, int fieldArrayIndex, String subFieldName) {
      final Field field = getField(num);

      if (field == null)
         return null;

      final SubField subField = field.getSubField(subFieldName);

      if ((subField == null) || (subField.canMesgSupport(this)))
         return field.getShortValue(fieldArrayIndex, subFieldName);
      else
         return null;
   }
   
   public Short getFieldShortValue(String name) {
      return getFieldShortValue(name, 0);
   }

   public Short getFieldShortValue(String name, int fieldArrayIndex) {
      final Field field = getField(name, false);

      if (field == null)
         return null;

      final SubField subField = field.getSubField(name);

      if ((subField == null) || (subField.canMesgSupport(this)))
         return field.getShortValue(fieldArrayIndex, name);
      else
         return null;
   }

   public Integer getFieldIntegerValue(int num) {
      return getFieldIntegerValue(num, 0, Fit.SUBFIELD_INDEX_ACTIVE_SUBFIELD);
   }

   public Integer getFieldIntegerValue(int num, int fieldArrayIndex) {
      return getFieldIntegerValue(num, fieldArrayIndex, Fit.SUBFIELD_INDEX_ACTIVE_SUBFIELD);
   }

   public Integer getFieldIntegerValue(int num, int fieldArrayIndex, int subFieldIndex) {
      final Field field = getField(num);

      if (field == null)
         return null;

      if (subFieldIndex == Fit.SUBFIELD_INDEX_ACTIVE_SUBFIELD) {
         return field.getIntegerValue(fieldArrayIndex, GetActiveSubFieldIndex(num));
      }
      else {
         final SubField subField = field.getSubField(subFieldIndex);
   
         if ((subField == null) || (subField.canMesgSupport(this)))
            return field.getIntegerValue(fieldArrayIndex, subFieldIndex);
         else
            return null;
      }
   }

   public Integer getFieldIntegerValue(int num, int fieldArrayIndex, String subFieldName) {
      final Field field = getField(num);

      if (field == null)
         return null;

      final SubField subField = field.getSubField(subFieldName);

      if ((subField == null) || (subField.canMesgSupport(this)))
         return field.getIntegerValue(fieldArrayIndex, subFieldName);
      else
         return null;
   }

   public Integer getFieldIntegerValue(String name) {
      return getFieldIntegerValue(name, 0);
   }

   public Integer getFieldIntegerValue(String name, int fieldArrayIndex) {
      final Field field = getField(name, false);

      if (field == null)
         return null;

      final SubField subField = field.getSubField(name);

      if ((subField == null) || (subField.canMesgSupport(this)))
         return field.getIntegerValue(fieldArrayIndex, name);
      else
         return null;
   }

   public Long getFieldLongValue(int num) {
      return getFieldLongValue(num, 0, Fit.SUBFIELD_INDEX_ACTIVE_SUBFIELD);
   }

   public Long getFieldLongValue(int num, int fieldArrayIndex) {
      return getFieldLongValue(num, fieldArrayIndex, Fit.SUBFIELD_INDEX_ACTIVE_SUBFIELD);
   }

   public Long getFieldLongValue(int num, int fieldArrayIndex, int subFieldIndex) {
      final Field field = getField(num);

      if (field == null)
         return null;

      if (subFieldIndex == Fit.SUBFIELD_INDEX_ACTIVE_SUBFIELD) {
         return field.getLongValue(fieldArrayIndex, GetActiveSubFieldIndex(num));
      }
      else {
         final SubField subField = field.getSubField(subFieldIndex);
   
         if ((subField == null) || (subField.canMesgSupport(this)))
            return field.getLongValue(fieldArrayIndex, subFieldIndex);
         else
            return null;
      }
   }

   public Long getFieldLongValue(int num, int fieldArrayIndex, String subFieldName) {
      final Field field = getField(num);

      if (field == null)
         return null;

      final SubField subField = field.getSubField(subFieldName);

      if ((subField == null) || (subField.canMesgSupport(this)))
         return field.getLongValue(fieldArrayIndex, subFieldName);
      else
         return null;
   }

   public Long getFieldLongValue(String name) {
      return getFieldLongValue(name, 0);
   }

   public Long getFieldLongValue(String name, int fieldArrayIndex) {
      final Field field = getField(name, false);

      if (field == null)
         return null;

      final SubField subField = field.getSubField(name);

      if ((subField == null) || (subField.canMesgSupport(this)))
         return field.getLongValue(fieldArrayIndex, name);
      else
         return null;
   }

   public Float getFieldFloatValue(int num) {
      return getFieldFloatValue(num, 0, Fit.SUBFIELD_INDEX_ACTIVE_SUBFIELD);
   }

   public Float getFieldFloatValue(int num, int fieldArrayIndex) {
      return getFieldFloatValue(num, fieldArrayIndex, Fit.SUBFIELD_INDEX_ACTIVE_SUBFIELD);
   }

   public Float getFieldFloatValue(int num, int fieldArrayIndex, int subFieldIndex) {
      final Field field = getField(num);

      if (field == null)
         return null;

      if (subFieldIndex == Fit.SUBFIELD_INDEX_ACTIVE_SUBFIELD) {
         return field.getFloatValue(fieldArrayIndex, GetActiveSubFieldIndex(num));
      }
      else {
         final SubField subField = field.getSubField(subFieldIndex);
   
         if ((subField == null) || (subField.canMesgSupport(this)))
            return field.getFloatValue(fieldArrayIndex, subFieldIndex);
         else
            return null;
      }
   }

   public Float getFieldFloatValue(int num, int fieldArrayIndex, String subFieldName) {
      final Field field = getField(num);

      if (field == null)
         return null;

      final SubField subField = field.getSubField(subFieldName);

      if ((subField == null) || (subField.canMesgSupport(this)))
         return field.getFloatValue(fieldArrayIndex, subFieldName);
      else
         return null;
   }

   public Float getFieldFloatValue(String name) {
      return getFieldFloatValue(name, 0);
   }

   public Float getFieldFloatValue(String name, int fieldArrayIndex) {
      final Field field = getField(name, false);

      if (field == null)
         return null;

      final SubField subField = field.getSubField(name);

      if ((subField == null) || (subField.canMesgSupport(this)))
         return field.getFloatValue(fieldArrayIndex, name);
      else
         return null;
   }

   public Double getFieldDoubleValue(int num) {
      return getFieldDoubleValue(num, 0, Fit.SUBFIELD_INDEX_ACTIVE_SUBFIELD);
   }

   public Double getFieldDoubleValue(int num, int fieldArrayIndex) {
      return getFieldDoubleValue(num, fieldArrayIndex, Fit.SUBFIELD_INDEX_ACTIVE_SUBFIELD);
   }

   public Double getFieldDoubleValue(int num, int fieldArrayIndex, int subFieldIndex) {
      final Field field = getField(num);

      if (field == null)
         return null;

      if (subFieldIndex == Fit.SUBFIELD_INDEX_ACTIVE_SUBFIELD) {
         return field.getDoubleValue(fieldArrayIndex, GetActiveSubFieldIndex(num));
      }
      else {
         final SubField subField = field.getSubField(subFieldIndex);
   
         if ((subField == null) || (subField.canMesgSupport(this)))
            return field.getDoubleValue(fieldArrayIndex, subFieldIndex);
         else
            return null;
      }
   }

   public Double getFieldDoubleValue(int num, int fieldArrayIndex, String subFieldName) {
      final Field field = getField(num);

      if (field == null)
         return null;

      final SubField subField = field.getSubField(subFieldName);

      if ((subField == null) || (subField.canMesgSupport(this)))
         return field.getDoubleValue(fieldArrayIndex, subFieldName);
      else
         return null;
   }

   public Double getFieldDoubleValue(String name) {
      return getFieldDoubleValue(name, 0);
   }

   public Double getFieldDoubleValue(String name, int fieldArrayIndex) {
      final Field field = getField(name, false);

      if (field == null)
         return null;

      final SubField subField = field.getSubField(name);

      if ((subField == null) || (subField.canMesgSupport(this)))
         return field.getDoubleValue(fieldArrayIndex, name);
      else
         return null;
   }

   public String getFieldStringValue(int num) {
      return getFieldStringValue(num, 0, Fit.SUBFIELD_INDEX_ACTIVE_SUBFIELD);
   }

   public String getFieldStringValue(int num, int fieldArrayIndex) {
      return getFieldStringValue(num, fieldArrayIndex, Fit.SUBFIELD_INDEX_ACTIVE_SUBFIELD);
   }

   public String getFieldStringValue(int num, int fieldArrayIndex, int subFieldIndex) {
      final Field field = getField(num);

      if (field == null)
         return null;

      if (subFieldIndex == Fit.SUBFIELD_INDEX_ACTIVE_SUBFIELD) {
         return field.getStringValue(fieldArrayIndex, GetActiveSubFieldIndex(num));
      }
      else {
         final SubField subField = field.getSubField(subFieldIndex);
   
         if ((subField == null) || (subField.canMesgSupport(this)))
            return field.getStringValue(fieldArrayIndex, subFieldIndex);
         else
            return null;
      }
   }

   public String getFieldStringValue(int num, int fieldArrayIndex, String subFieldName) {
      final Field field = getField(num);

      if (field == null)
         return null;

      final SubField subField = field.getSubField(subFieldName);

      if ((subField == null) || (subField.canMesgSupport(this)))
         return field.getStringValue(fieldArrayIndex, subFieldName);
      else
         return null;
   }

   public String getFieldStringValue(String name) {
      return getFieldStringValue(name, 0);
   }

   public String getFieldStringValue(String name, int fieldArrayIndex) {
      final Field field = getField(name, false);

      if (field == null)
         return null;

      final SubField subField = field.getSubField(name);

      if ((subField == null) || (subField.canMesgSupport(this)))
         return field.getStringValue(fieldArrayIndex, name);
      else
         return null;
   }

   public Collection<Field> getFields() {
      return Collections.unmodifiableCollection(fields);
   }

   public DateTime timestampToDateTime(Long timestamp) {
      DateTime dateTime;

      if (timestamp == null)
         return null;

      dateTime = new DateTime(timestamp);
      dateTime.convertSystemTimeToUTC(systemTimeOffset);

      return dateTime;
   }
   
   public void setLocalNum(int localNum)
   {
      if (localNum >= Fit.MAX_LOCAL_MESGS)
         throw new FitRuntimeException("Invalid local message number " + localNum + ".  Local message number must be < " + Fit.MAX_LOCAL_MESGS + ".");

      this.localNum = localNum;
   }

   public int getLocalNum()
   {
      return localNum;
   }
}
