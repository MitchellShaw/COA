package Controller;


import java.io.IOException;
import java.net.MalformedURLException;

import java.net.URL;
import java.util.*;

import Model.Functions;
import Model.Order;
import insidefx.undecorator.UndecoratorScene;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.Duration;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * @author Ramon Johnson
 * @version 0.0.0.1
 * 8/25/2017
 */
public class DashboardViewController implements Initializable
{

    /**
     * SessionFactory object to create Session's to run queries
     */
    private SessionFactory sessionFactory;

    /**
     * Stage object to close the Stage when the exit window is closed
     */
    private Stage stage;

    // ---https://stackoverflow.com/questions/18618653/binding-hashmap-with-tableview-javafx ---//
    private ObservableList<Order> data = FXCollections.observableArrayList();

    /**
     * Setter for property 'stage'.
     *
     * @param _stage Value to set for property 'stage'.
     */
    void setStage(Stage _stage)
    {
        stage = _stage;
    }

    DashboardViewController(SessionFactory _factory)
    {
        sessionFactory = _factory;
    }


    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="pane"
    private GridPane pane; // Value injected by FXMLLoader

    @FXML // fx:id="openOrder"
    private Label openOrder; // Value injected by FXMLLoader

    @FXML // fx:id="buttonBar"
    private ButtonBar buttonBar; // Value injected by FXMLLoader

    @FXML // fx:id="exitButton"
    private Button exitButton; // Value injected by FXMLLoader

    @FXML // fx:id="editButton"
    private Button editButton; // Value injected by FXMLLoader

    @FXML // fx:id="completeOrderButton"
    private Button completeOrderButton; // Value injected by FXMLLoader

    @FXML // fx:id="table"
    private TableView<Order> table; // Value injected by FXMLLoader

    @FXML // fx:id="orderNumberColumn"
    private TableColumn<Order, String> orderNumberColumn; // Value injected by FXMLLoader

    @FXML // fx:id="quantityColumn"
    private TableColumn<Order, String> quantityColumn; // Value injected by FXMLLoader

    @FXML // fx:id="quantityRemainingColumn"
    private TableColumn<Order, String> quantityRemainingColumn; // Value injected by FXMLLoader

    @FXML // fx:id="quantityRemainingColumn1"
    private TableColumn<Order, String> ssdColumn; // Value injected by FXMLLoader

    @FXML
    void completeOrder(ActionEvent event)
    {
        event.consume();
        Order temp = table.getSelectionModel().getSelectedItem();
        if(temp != null)
        {
            if((temp.getQuantity() - temp.getCompleted()) > 0)
            {
                new Alert(Alert.AlertType.ERROR, "There are still more COA's that need to be assigned.", ButtonType.CLOSE).showAndWait();
            }
            else
            {
                if((temp.getQuantity() - temp.getCompleted()) == 0)
                {
                    Session session = sessionFactory.openSession();
                    session.getTransaction().begin();
                    Query query = session.createQuery("from Order WHERE orderNumber = :_orderNumber").setParameter("_orderNumber", temp.getOrderNumber());;
                    List<Order> orders = query.list();
                    for(Order o : orders)
                    {
                        o.setFinished(true);
                        session.save(o);
                        session.getTransaction().commit();
                    }
                    session.close();
                }
            }
        }
    }


    @FXML
    void editOrder(ActionEvent event) throws IOException
    {
        event.consume();
        Order temp = table.getSelectionModel().getSelectedItem();
        if(temp != null)
        {
            Stage editStage = new Stage(StageStyle.UNDECORATED);
            editStage.initModality(Modality.APPLICATION_MODAL);
            String testPath = String.format("%s\n", getClass().getResource("DashboardViewController.class"));
            String[] path = testPath.split("classes");
            FXMLLoader loader = new FXMLLoader(new URL(path[0] + "resources/FXML's/EditOrderView.fxml"));
            EditOrderViewController editOrderViewController = new EditOrderViewController(sessionFactory, editStage, temp.getOrderNumber(), temp.getScheduledShipDate(), temp.getQuantity());
            loader.setController(editOrderViewController);
            GridPane gridPane = loader.load();
            Functions.setUpIcons(editStage);
            editStage.setOnCloseRequest(event1 -> editStage.close());
            editStage.setOnCloseRequest(event1 ->
            {
                editStage.close();
            });
            editStage.setScene(new Scene(gridPane));
            editStage.show();
        }
    }

    @FXML
    void exitView(ActionEvent event)
    {
        System.exit(0);
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize()
    {
        assert pane != null : "fx:id=\"pane\" was not injected: check your FXML file 'DashboardView.fxml'.";
        assert openOrder != null : "fx:id=\"openOrder\" was not injected: check your FXML file 'DashboardView.fxml'.";
        assert buttonBar != null : "fx:id=\"buttonBar\" was not injected: check your FXML file 'DashboardView.fxml'.";
        assert exitButton != null : "fx:id=\"exitButton\" was not injected: check your FXML file 'DashboardView.fxml'.";
        assert editButton != null : "fx:id=\"editButton\" was not injected: check your FXML file 'DashboardView.fxml'.";
        assert completeOrderButton != null : "fx:id=\"submitButton\" was not injected: check your FXML file 'DashboardView.fxml'.";
        assert table != null : "fx:id=\"table\" was not injected: check your FXML file 'DashboardView.fxml'.";
        assert orderNumberColumn != null : "fx:id=\"orderNumberColumn\" was not injected: check your FXML file 'DashboardView.fxml'.";
        assert quantityColumn != null : "fx:id=\"quantityColumn\" was not injected: check your FXML file 'DashboardView.fxml'.";
        assert quantityRemainingColumn != null : "fx:id=\"quantityRemainingColumn\" was not injected: check your FXML file 'DashboardView.fxml'.";
        assert ssdColumn != null : "fx:id=\"ssdColumn\" was not injected: check your FXML file 'DashboardView.fxml'.";
    }

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  <tt>null</tt> if the location is not known.
     * @param resources The resources used to localize the root object, or <tt>null</tt> if
     */
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        initialize();
        setUpTableColumns();
        table.setItems(data);
        BackgroundService backgroundService = new BackgroundService();
        backgroundService.setRestartOnFailure(true);
        backgroundService.setPeriod(Duration.millis(500));
        SetCompletedService setCompletedService = new SetCompletedService();
        setCompletedService.setRestartOnFailure(true);
        setCompletedService.setPeriod(Duration.seconds(2));
        backgroundService.start();
        setCompletedService.start();
    }

    private void setUpTableColumns()
    {
        orderNumberColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Order, String>, ObservableValue<String>>()
        {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Order, String> param)
            {
                return new SimpleStringProperty(String.valueOf(param.getValue().getOrderNumber()));
            }
        });
        quantityColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Order, String>, ObservableValue<String>>()
        {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Order, String> param)
            {
                return new SimpleStringProperty(String.valueOf(param.getValue().getQuantity()));
            }
        });

        quantityRemainingColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Order, String>, ObservableValue<String>>()
        {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Order, String> param)
            {
                return new SimpleStringProperty(String.valueOf(param.getValue().getQuantity() - param.getValue().getCompleted()));
            }
        });

        ssdColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Order, String>, ObservableValue<String>>()
        {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Order, String> param)
            {
                return new SimpleStringProperty(String.valueOf(param.getValue().getScheduledShipDate().toString()));
            }
        });


        /*

         Old way of doing things. It's best if Map is used as observable
        orderNumberColumn.setCellValueFactory(new PropertyValueFactory<OrderProperty, String>("orderNumber"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<OrderProperty, String>("quantity"));
        quantityRemainingColumn.setCellValueFactory(new PropertyValueFactory<OrderProperty, String>("quantityRemaining"));
        ssdColumn.setCellValueFactory(new PropertyValueFactory<OrderProperty, String>("scheduleShipDate"));
        //--- Testing purposes, trying to see what the graph would look like ---//
        /*OrderProperty one = new OrderProperty();
        one.setOrderNumber(String.valueOf(12345678));
        one.setQuantity("5");
        one.setQuantityRemaining("3");
        one.setScheduleShipDate("2017/08/25");

        OrderProperty two = new OrderProperty();
        two.setOrderNumber(String.valueOf(23456789));
        two.setQuantity("10");
        two.setQuantityRemaining("7");
        two.setScheduleShipDate("2017/08/26");

        OrderProperty three = new OrderProperty();
        three.setOrderNumber(String.valueOf(47568456));
        three.setQuantity("47");
        three.setQuantityRemaining("25");
        three.setScheduleShipDate("2017/08/29");
        ObservableList<OrderProperty> orders = FXCollections.observableArrayList();
        orders.addAll(one, two, three,one ,two ,three,one, two, three,one ,two ,three,one, two, three,one ,two ,three,one, two, three,one ,two ,three,one, two, three,one ,two ,three,one, two, three,one ,two ,three,one, two, three,one ,two ,three,one, two, three,one ,two ,three,one, two, three,one ,two ,three,one, two, three,one ,two ,three,one, two, three,one ,two ,three,one, two, three,one ,two ,three,one, two, three,one ,two ,three,one, two, three,one ,two ,three,one, two, three,one ,two ,three,one, two, three,one ,two ,three);
        table.setItems(orders);*/
    }

    private class SetCompletedService extends ScheduledService<Void>
    {

        /**
         * Invoked after the Service is started on the JavaFX Application Thread.
         * Implementations should save off any state into final variables prior to
         * creating the Task, since accessing properties defined on the Service
         * within the background thread code of the Task will result in exceptions.
         * <p>
         * For example:
         * <pre><code>
         *     protected Task createTask() {
         *         final String url = myService.getUrl();
         *         return new Task&lt;String&gt;() {
         *             protected String call() {
         *                 URL u = new URL("http://www.oracle.com");
         *                 BufferedReader in = new BufferedReader(
         *                         new InputStreamReader(u.openStream()));
         *                 String result = in.readLine();
         *                 in.close();
         *                 return result;
         *             }
         *         }
         *     }
         * </code></pre>
         * <p>
         * <p>
         * If the Task is a pre-defined class (as opposed to being an
         * anonymous class), and if it followed the recommended best-practice,
         * then there is no need to save off state prior to constructing
         * the Task since its state is completely provided in its constructor.
         * </p>
         * <p>
         * <pre><code>
         *     protected Task createTask() {
         *         // This is safe because getUrl is called on the FX Application
         *         // Thread and the FirstLineReaderTasks stores it as an
         *         // immutable property
         *         return new FirstLineReaderTask(myService.getUrl());
         *     }
         * </code></pre>
         *
         * @return the Task to execute
         */
        @Override
        protected Task<Void> createTask()
        {
            return new Task<Void>()
            {
                @Override
                protected Void call() throws Exception
                {
                    Session session = sessionFactory.openSession();
                    session.getTransaction().begin();
                    if(data.size() > 0)
                    {
                        for(Order $order: data)
                        {
                            int orderNumber = $order.getOrderNumber();
                            Query query = session.createQuery("from COA WHERE order.orderNumber= :number").setParameter("number", orderNumber);
                            $order.setCompleted(query.list().size());
                        }
                    }
                    session.close();
                    return null;
                }
            };
        }
    }

    private class BackgroundService extends ScheduledService<Void>
    {

        /**
         * Invoked after the Service is started on the JavaFX Application Thread.
         * Implementations should save off any state into final variables prior to
         * creating the Task, since accessing properties defined on the Service
         * within the background thread code of the Task will result in exceptions.
         * <p>
         * For example:
         * <pre><code>
         *     protected Task createTask() {
         *         final String url = myService.getUrl();
         *         return new Task&lt;String&gt;() {
         *             protected String call() {
         *                 URL u = new URL("http://www.oracle.com");
         *                 BufferedReader in = new BufferedReader(
         *                         new InputStreamReader(u.openStream()));
         *                 String result = in.readLine();
         *                 in.close();
         *                 return result;
         *             }
         *         }
         *     }
         * </code></pre>
         * <p>
         * <p>
         * If the Task is a pre-defined class (as opposed to being an
         * anonymous class), and if it followed the recommended best-practice,
         * then there is no need to save off state prior to constructing
         * the Task since its state is completely provided in its constructor.
         * </p>
         * <p>
         * <pre><code>
         *     protected Task createTask() {
         *         // This is safe because getUrl is called on the FX Application
         *         // Thread and the FirstLineReaderTasks stores it as an
         *         // immutable property
         *         return new FirstLineReaderTask(myService.getUrl());
         *     }
         * </code></pre>
         *
         * @return the Task to execute
         */
        @Override
        protected Task<Void> createTask()
        {
            return new Task<Void>()
            {
                @Override
                protected Void call() throws Exception
                {
                    Session session = sessionFactory.openSession();
                    session.getTransaction().begin();
                    Query query = session.createQuery("from Order WHERE isFinished = false");
                    List<Order> orderList = query.list();
                    if(orderList.size() > data.size())
                        databaseGreaterThanMap(orderList, data);
                    else if(orderList.size() < data.size())
                        databaseLesserThanMap(orderList, data);
                    else if(orderList.size() == data.size())
                        databaseEqualToMap(orderList, data);
                    else
                    {
                        data = FXCollections.observableArrayList();
                    }
                    session.close();
                    table.refresh();
                    return null;
                }
            };
        }

        /* This is how we add stuff to the data
        Map<Integer, Order> testing = new TreeMap<>();
        Order one = new Order();
        one.setOrderNumber(1234567);
        one.setFinished(false);
        one.setQuantity(5);
        one.setScheduledShipDate(LocalDate.now());
        Order two = new Order();
        two.setOrderNumber(2345678);
        two.setFinished(false);
        two.setQuantity(47);
        two.setScheduledShipDate(LocalDate.now());

        testing.put(1234567, one);
        testing.put(2345678, two);

        data = FXCollections.observableArrayList(testing.entrySet());
        data.get(0).getValue().getOrderNumber();*/

        /**
         * @param _order List from the Query
         * @param _orders List saved that is set to the Program
         */
        private void databaseGreaterThanMap(List<Order> _order, ObservableList<Order> _orders)
        {
            //--- This method gets activated by the list from the Query being bigger than the data saved ---//
            Map<Integer, Order> $orderMap = orderMap(_order);
            Map<Integer, Order> $observableMap = orderMap(_orders);
            //-- Since _orderMap is greater, we can easily add to observable ---//
            for(Integer $order : $orderMap.keySet())
            {
                if($observableMap.containsKey($order))
                {
                    Order temp = $orderMap.get($order);
                    Order data = $observableMap.get($order);
                    data.setQuantity(temp.getQuantity());
                    data.setScheduledShipDate(temp.getScheduledShipDate());
                    //System.out.printf("********************************GREATER THAN**************************\n%s | %s\n",temp.toString(), data.toString() );
                }
                else
                {
                    _orders.add($orderMap.get($order));
                    //System.out.printf("********************************NEW********************************\n%s\n",$orderMap.get($order).toString() );
                }
            }
        }

        /**
         * @param _order List from the Query
         * @param _orders List saved that is set to the Program
         */
        private void databaseLesserThanMap(List<Order> _order, ObservableList<Order> _orders)
        {
            //--- This method is activated when the list from the Query is less than the data saved ---//
            Map<Integer, Order> $orderMap = orderMap(_order);
            Map<Integer, Order> $observableMap = orderMap(_orders);
            //--- We have to remove from observable List
            for (Integer $order : $orderMap.keySet())
            {
                if($observableMap.containsKey($order))
                {
                    Order temp = $orderMap.get($order);
                    Order data = $observableMap.get($order);
                    data.setQuantity(temp.getQuantity());
                    data.setScheduledShipDate(temp.getScheduledShipDate());
                    //System.out.printf("********************************LESSER THAN**************************\n%s | %s\n",temp.toString(), data.toString() );
                }
            }

            for(Integer $order : $observableMap.keySet())
            {
                if(!$orderMap.containsKey($order))
                {
                    _orders.remove($observableMap.get($order));
                }
            }
        }

        /**
         * @param _order List from the Query
         * @param _orders List saved that is set to the Program
         */
        private void databaseEqualToMap(List<Order> _order, ObservableList<Order> _orders)
        {
            //--- This method is activated if both list are the same, just send the details over to observable ---//
            Map<Integer, Order> $orderMap = orderMap(_order);
            Map<Integer, Order> $observableMap = orderMap(_orders);
            for(Integer $order: $observableMap.keySet())
            {
                //--- There is a possibility for null here, but it should be very very very hard to do so ---//
                Order temp = $orderMap.get($order);
                Order data = $observableMap.get($order);
                data.setQuantity(temp.getQuantity());
                data.setScheduledShipDate(temp.getScheduledShipDate());
                //System.out.printf("********************************EQUAL********************************\n%s | %s\n",temp.toString(), data.toString() );
            }
        }

        private Map<Integer, Order>orderMap(List<Order> _theList)
        {
            Map<Integer, Order> _temp = new TreeMap<>();
            for(Order _order: _theList)
            {
                _temp.put(_order.getOrderNumber(), _order);
            }
            return _temp;
        }

        private Map<Integer, Order>orderMap(ObservableList<Order> _theList)
        {
            Map<Integer, Order> _temp = new TreeMap<>();
            for(Order _order: _theList)
            {
                _temp.put(_order.getOrderNumber(), _order);
            }
            return _temp;
        }
    }
}