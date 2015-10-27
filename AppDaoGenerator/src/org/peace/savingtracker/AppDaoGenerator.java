/*
 * Copyright (C) 2011 Markus Junginger, greenrobot (http://greenrobot.de)
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
 */
package org.peace.savingtracker;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

/**
 * Generates entities and DAOs for the example project DaoExample.
 *
 * Run it as a Java application (not Android).
 *
 * @author Markus
 */
public class AppDaoGenerator {

  public static void main(String[] args) throws Exception {
    Schema schema = new Schema(1, "org.peace.savingtracker.dao");

    addExpense(schema);

    new DaoGenerator().generateAll(schema, "../app/src/main/java");
  }

  private static void addExpense(Schema schema) {
    Entity expense = schema.addEntity("Expense");
    expense.addIdProperty();
    expense.addStringProperty("user_id").notNull();
    expense.addStringProperty("name");
    expense.addLongProperty("date").notNull();
    expense.addStringProperty("category");
    expense.addDoubleProperty("value").notNull();
  }

  private static void addNote(Schema schema) {
    Entity note = schema.addEntity("Note");
    note.addIdProperty();
    note.addStringProperty("text").notNull();
    note.addStringProperty("comment");
    note.addDateProperty("date");
  }

  private static void addCustomerOrder(Schema schema) {
    Entity customer = schema.addEntity("Customer");
    customer.addIdProperty();
    customer.addStringProperty("name").notNull();

    Entity order = schema.addEntity("Order");
    order.setTableName("ORDERS"); // "ORDER" is a reserved keyword
    order.addIdProperty();
    Property orderDate = order.addDateProperty("date").getProperty();
    Property customerId = order.addLongProperty("customerId").notNull().getProperty();
    order.addToOne(customer, customerId);

    ToMany customerToOrders = customer.addToMany(order, customerId);
    customerToOrders.setName("orders");
//    customerToOrders.orderAsc(orderDate);
  }
}
