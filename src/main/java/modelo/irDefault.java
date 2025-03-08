package modelo;

import javafx.beans.property.*;

public class irDefault {
  private final IntegerProperty id;
  private final IntegerProperty fieldId;
  private final StringProperty condition;
  private final StringProperty jsonValue;

  public irDefault(int id, int fieldId, String condition, String jsonValue) {
    this.id = new SimpleIntegerProperty(id);
    this.fieldId = new SimpleIntegerProperty(fieldId);
    this.condition = new SimpleStringProperty(condition);
    this.jsonValue = new SimpleStringProperty(jsonValue);
  }

  public int getId(){
    return id.get();
  }
  public int getFieldId() {
    return fieldId.get();
  }
  public String getCondition(){
    return condition.get();
  }
  public String getJsonValue() {
    return jsonValue.get();
  }

  public void setId(int id) { this.id.set(id); }
  public void setFieldId(int fieldId) { this.fieldId.set(fieldId); }
  public void setCondition(String condition) { this.condition.set(condition); }
  public void setJsonValue(String jsonValue) { this.jsonValue.set(jsonValue); }

  public IntegerProperty idProperty() { return id; }
  public IntegerProperty fieldIdProperty() { return fieldId; }
  public StringProperty conditionProperty() { return condition; }
  public StringProperty jsonValueProperty() { return jsonValue; }
}