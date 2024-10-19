import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;  
import java.util.List;       
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class FashionShopGUI {
	
	private static Map<String, Order> orderDatabase = new HashMap<>();
	private static List<Customer> customers = new ArrayList<>(); // List to store customers
	public static List<Order> orders = new ArrayList<>();  // List to store orders
	
	public enum OrderStatus {
			PROCESSING, DELIVERING, DELIVERED;
	}
    
    private static double getPriceBySize(String size) {
        switch (size) {
            case "XS": return 600;
            case "S": return 800;
            case "M": return 900;
            case "L": return 1000;
            case "XL": return 1100;
            case "XXL": return 1200;
            default: return -1;
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(FashionShopGUI::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Fashion Shop");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel titleLabel = new JLabel("Fashion Shop", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(0, 128, 255));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(0, 20, 500, 50);
        panel.add(titleLabel);

        JButton searchButton = new JButton("Search");
        searchButton.setBounds(50, 100, 150, 30);
        panel.add(searchButton);

        JButton statusButton = new JButton("Status");
        statusButton.setBounds(50, 150, 150, 30);
        panel.add(statusButton);

        JButton reportsButton = new JButton("Reports");
        reportsButton.setBounds(50, 200, 150, 30);
        panel.add(reportsButton);

        JButton deleteButton = new JButton("Delete");
        deleteButton.setBounds(50, 250, 150, 30);
        panel.add(deleteButton);

        JButton placeOrderButton = new JButton("Place Order");
        placeOrderButton.setBounds(50, 320, 150, 60);
        placeOrderButton.setFont(new Font("Arial", Font.BOLD, 18));
        placeOrderButton.setBackground(new Color(0, 204, 204));
        placeOrderButton.setForeground(Color.BLACK);
        panel.add(placeOrderButton);

        ImageIcon imageIcon = new ImageIcon("fashion_shop_image.png");
        JLabel imageLabel = new JLabel(imageIcon);
        imageLabel.setBounds(260, 100, 200, 300);
        panel.add(imageLabel);

        JLabel copyrightLabel = new JLabel("Copyrights \u00A9 iCET 2023");
        copyrightLabel.setBounds(160, 400, 200, 30);
        panel.add(copyrightLabel);

        searchButton.addActionListener(e -> showSearchOptions());
        
        statusButton.addActionListener(e -> new ChangeOrderStatus());
        
        reportsButton.addActionListener(e -> new ViewReports());
        
        deleteButton.addActionListener(e -> new DeleteOrderGUI());

        placeOrderButton.addActionListener(e -> new PlaceOrderGUI());

        frame.add(panel);
        frame.setVisible(true);
    }

    private static void showSearchOptions() {
        String[] options = {"Search Customer", "Search Order", "Cancel"};
        int choice = JOptionPane.showOptionDialog(null, "Please select the option",
                "Search Options", JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        if (choice == 0) {
            new searchCustomer();
        } else if (choice == 1) {
            new searchOrder();
        }
    }

	public static class searchCustomer extends JFrame {
		private JLabel totalLabel;
		private JLabel totalValue;
		private JTable orderTable;
		private DefaultTableModel tableModel;

		public searchCustomer() {
			setTitle("Search Customer");
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setSize(600, 400);
			setLayout(null);

			JLabel customerIdLabel = new JLabel("Enter Customer ID: ");
			customerIdLabel.setBounds(40, 40, 150, 30);
			add(customerIdLabel);

			JTextField customerIdField = new JTextField();
			customerIdField.setBounds(170, 40, 200, 30);
			add(customerIdField);

			totalLabel = new JLabel("Total: ");
			totalLabel.setBounds(30, 320, 100, 30);
			add(totalLabel);

			totalValue = new JLabel("");
			totalValue.setBounds(480, 320, 200, 30);
			add(totalValue);

			JButton backButton = new JButton("Back");
			backButton.setBounds(10, 10, 80, 30);
			backButton.setBackground(new Color(255, 102, 102));
			backButton.setForeground(Color.WHITE);
			add(backButton);

			JButton searchButton = new JButton("Search");
			searchButton.setBounds(390, 40, 80, 30);
			searchButton.setBackground(new Color(0, 153, 153));
			searchButton.setForeground(Color.WHITE);
			add(searchButton);

			String[] columnNames = {"Size", "QTY", "Amount"};
			tableModel = new DefaultTableModel(columnNames, 0);
			orderTable = new JTable(tableModel);
			JScrollPane scrollPane = new JScrollPane(orderTable);
			scrollPane.setBounds(30, 80, 510, 230);
			add(scrollPane);

			searchButton.addActionListener(e -> {
				String customerId = customerIdField.getText();
				
				// Clear existing data
				tableModel.setRowCount(0);
				totalValue.setText("");
				
					// Find orders by customer ID
					List<Order> customerOrders = FashionShopGUI.orders.stream()
							.filter(order -> order.customerId.equals(customerId))
							.collect(Collectors.toList());

					// Populate table and calculate total
					double totalAmount = 0.0;
					for (Order order : customerOrders) {
						Object[] row = {order.size, order.quantity, order.amount};
						tableModel.addRow(row);
						totalAmount += order.amount;
					}

				// Update total value
				totalValue.setText(String.format("%.2f", totalAmount));	

			});

			backButton.addActionListener(e -> dispose());

			setLocationRelativeTo(null);
			setVisible(true);
		}
		
		private static Customer findCustomerById(String customerId) {
			for (Customer customer : customers) {
				if (customer.customerId.equals(customerId)) {
					return customer;
				}
			}
			return null; // Customer not found
		}
	}

    public static class searchOrder extends JFrame {
		
		public searchOrder() {
			setTitle("Search Order");
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setSize(490, 400);
			setLayout(null);

			JLabel orderIdLabel = new JLabel("Enter Order ID: ");
			orderIdLabel.setBounds(30, 80, 100, 30);
			add(orderIdLabel);

			JTextField orderIdField = new JTextField();
			orderIdField.setBounds(150, 80, 200, 30);
			add(orderIdField);

			JLabel customerIdLabel = new JLabel("Customer ID :");
			customerIdLabel.setBounds(30, 130, 100, 30);
			add(customerIdLabel);

			JLabel customerIdValue = new JLabel("");
			customerIdValue.setBounds(150, 130, 200, 30);
			add(customerIdValue);

			JLabel sizeLabel = new JLabel("Size :");
			sizeLabel.setBounds(30, 170, 100, 30);
			add(sizeLabel);

			JLabel sizeValue = new JLabel("");
			sizeValue.setBounds(150, 170, 200, 30);
			add(sizeValue);

			JLabel qtyLabel = new JLabel("QTY :");
			qtyLabel.setBounds(30, 210, 100, 30);
			add(qtyLabel);

			JLabel qtyValue = new JLabel("");
			qtyValue.setBounds(150, 210, 200, 30);
			add(qtyValue);

			JLabel amountLabel = new JLabel("Amount :");
			amountLabel.setBounds(30, 250, 100, 30);
			add(amountLabel);

			JLabel amountValue = new JLabel("");
			amountValue.setBounds(150, 250, 200, 30);
			add(amountValue);

			JLabel statusLabel = new JLabel("Status :");
			statusLabel.setBounds(30, 290, 100, 30);
			add(statusLabel);

			JLabel statusValue = new JLabel("");
			statusValue.setBounds(150, 290, 200, 30);
			add(statusValue);

			JButton backButton = new JButton("Back");
			backButton.setBounds(20, 20, 80, 30);
			backButton.setBackground(new Color(255, 102, 102));
			backButton.setForeground(Color.WHITE);
			add(backButton);

			JButton searchButton = new JButton("Search");
			searchButton.setBounds(360, 80, 80, 30);
			searchButton.setBackground(new Color(0, 153, 153));
			searchButton.setForeground(Color.WHITE);
			add(searchButton);		

			searchButton.addActionListener(e -> {
				String orderId = orderIdField.getText();
				Order order = findOrderById(orderId);
				if (order != null) {
					customerIdValue.setText(order.customerId);
					sizeValue.setText(order.size);
					qtyValue.setText(String.valueOf(order.quantity));
					amountValue.setText(String.valueOf(order.amount));
					statusValue.setText(order.status.toString());
				} else {
					JOptionPane.showMessageDialog(this, "Invalid Order ID", "Error", JOptionPane.ERROR_MESSAGE);
				}
			});

			backButton.addActionListener(e -> dispose());

			setLocationRelativeTo(null);
			setVisible(true);	
		}
		
		private Order findOrderById(String orderId) {
			for (Order order : FashionShopGUI.orders) {
				if (order.orderId.equals(orderId)) {
					return order;
				}
			}
			return null; // Order not found
		}
	}

	public static class ChangeOrderStatus extends JFrame {
		private JLabel statusLabel; // Declare statusLabel at the class level

		public ChangeOrderStatus() {
			setTitle("Status Form");
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setSize(490, 420);
			setLayout(null);

			JLabel orderIdLabel = new JLabel("Enter Order ID : ");
			orderIdLabel.setBounds(30, 80, 100, 30);
			add(orderIdLabel);

			JTextField orderIdField = new JTextField();
			orderIdField.setBounds(150, 80, 200, 30);
			add(orderIdField);

			JLabel customerIdLabel = new JLabel("Customer ID : ");
			customerIdLabel.setBounds(30, 130, 100, 30);
			add(customerIdLabel);

			JLabel customerIdValue = new JLabel("");
			customerIdValue.setBounds(150, 130, 200, 30);
			add(customerIdValue);

			JLabel sizeLabel = new JLabel("Size : ");
			sizeLabel.setBounds(30, 170, 100, 30);
			add(sizeLabel);

			JLabel sizeValue = new JLabel("");
			sizeValue.setBounds(150, 170, 200, 30);
			add(sizeValue);

			JLabel qtyLabel = new JLabel("QTY : ");
			qtyLabel.setBounds(30, 210, 100, 30);
			add(qtyLabel);

			JLabel qtyValue = new JLabel("");
			qtyValue.setBounds(150, 210, 200, 30);
			add(qtyValue);

			JLabel amountLabel = new JLabel("Amount : ");
			amountLabel.setBounds(30, 250, 100, 30);
			add(amountLabel);

			JLabel amountValue = new JLabel("");
			amountValue.setBounds(150, 250, 200, 30);
			add(amountValue);
			
			statusLabel = new JLabel("Status : "); // Initialize as a member variable
			statusLabel.setBounds(30, 290, 100, 30);
			add(statusLabel);

			JButton backButton = new JButton("Back");
			backButton.setBounds(20, 20, 80, 30);
			backButton.setBackground(new Color(255, 102, 102));
			backButton.setForeground(Color.WHITE);
			add(backButton);

			JButton searchButton = new JButton("Search");
			searchButton.setBounds(360, 80, 80, 30);
			searchButton.setBackground(new Color(0, 153, 153));
			searchButton.setForeground(Color.WHITE);
			add(searchButton);        

			JButton changeStatusButton = new JButton("Change Status");
			changeStatusButton.setBounds(330, 320, 130, 30);
			changeStatusButton.setBackground(new Color(51, 153, 255));
			changeStatusButton.setForeground(Color.WHITE);
			add(changeStatusButton);

			 // Action listener for the Search button
			searchButton.addActionListener(e -> {
            String orderId = orderIdField.getText();
            if (orderDatabase.containsKey(orderId)) {
                Order order = orderDatabase.get(orderId);
                customerIdLabel.setText("Customer ID: " + order.customerId);
                sizeLabel.setText("Size: " + order.size);
                qtyLabel.setText("Quantity: " + order.quantity);
                double totalAmount = getPriceBySize(order.size) * order.quantity;
                amountLabel.setText("Amount: " + String.format("%.2f", totalAmount));
                statusLabel.setText("Status: " + order.status);
            } else {
                JOptionPane.showMessageDialog(this, "Order not found!");
            }
        });
        
			// Action listener for the Change Status button
			changeStatusButton.addActionListener(e -> {
				String orderId = orderIdField.getText();
				if (orderDatabase.containsKey(orderId)) {
					Order order = orderDatabase.get(orderId);
					
					// Handle status changes based on the current status
					if (order.status.equals("PROCESSING")) {
						showStatusSelectionDialog(order, true); // Show both buttons
					} else if (order.status.equals("DELIVERING")) {
						showStatusSelectionDialog(order, false); // Show only delivered button
					} else {
						JOptionPane.showMessageDialog(this, "No status change available for the current status.");
					}
				} else {
					JOptionPane.showMessageDialog(this, "Order not found!");
				}
			});
			
			backButton.addActionListener(e -> dispose());
			
			setLocationRelativeTo(null);
			setVisible(true);
		}

		private double getPriceBySize(String size) {
			switch (size) {
				case "XS": return 600;
				case "S": return 800;
				case "M": return 900;
				case "L": return 1000;
				case "XL": return 1100;
				case "XXL": return 1200;
				default: return -1; // Or throw an exception if size is invalid
			}
		}

		private void showStatusSelectionDialog(Order order, boolean showDelivering) {
			JDialog statusDialog = new JDialog(this, "Select Order Status", true);
			statusDialog.setSize(300, 200);
			statusDialog.setLayout(new GridLayout(3, 1)); // 3 rows, 1 column

			JLabel instructionLabel = new JLabel("Please select the status:");
			instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
			statusDialog.add(instructionLabel);

			JPanel buttonPanel = new JPanel(new GridLayout(1, showDelivering ? 2 : 1)); // 1 row, 2 columns if delivering is shown
			if (showDelivering) {
				JButton deliveringButton = new JButton("Delivering");
				deliveringButton.addActionListener(e -> {
					order.status = "DELIVERING";
					JOptionPane.showMessageDialog(statusDialog, "Order status changed to 'DELIVERING'.");
					statusLabel.setText("Status: " + order.status); // Update the main status label
					statusDialog.dispose();
				});
				buttonPanel.add(deliveringButton);
			}

			JButton deliveredButton = new JButton("Delivered");
			deliveredButton.addActionListener(e -> {
				order.status = "DELIVERED";
				JOptionPane.showMessageDialog(statusDialog, "Order status changed to 'DELIVERED'.");
				statusLabel.setText("Status: " + order.status); // Update the main status label
				statusDialog.dispose();
			});
			
			buttonPanel.add(deliveredButton);
			
			statusDialog.add(buttonPanel);
			statusDialog.setLocationRelativeTo(this);
			statusDialog.setVisible(true);
		}
	}

	public static class ViewReports extends JFrame {
		
		public ViewReports() {
			int i = 0;
			setTitle("View Reports");
			setSize(610, 220);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setLayout(null); // Using null layout for precise positioning

			// Back button
			JButton backButton = new JButton("Back");
			backButton.setBounds(10, 10, 70, 30);
			backButton.setBackground(new Color(255, 102, 102));
			backButton.setForeground(Color.WHITE);
			add(backButton);

			// Green buttons
			JButton viewCustomerButton = new JButton("View Customers");
			viewCustomerButton.setBackground(Color.GREEN);
			viewCustomerButton.setForeground(Color.WHITE);
			viewCustomerButton.setBounds(10, 50, 180, 30);
			add(viewCustomerButton);
				
			JButton bestCustomerButton = new JButton("Best In Customers");
			bestCustomerButton.setBackground(Color.GREEN);
			bestCustomerButton.setForeground(Color.WHITE);
			bestCustomerButton.setBounds(10, 90, 180, 30);
			add(bestCustomerButton);
				
			JButton allCustomersButton = new JButton("All Customers");
			allCustomersButton.setBackground(Color.GREEN);
			allCustomersButton.setForeground(Color.WHITE);
			allCustomersButton.setBounds(10, 130, 180, 30);	
			add(allCustomersButton);	

			// Blue buttons
			JButton categorizedByQtyButton = new JButton("Categorized By QTY");
            categorizedByQtyButton.setBackground(Color.BLUE);
            categorizedByQtyButton.setForeground(Color.WHITE);
            categorizedByQtyButton.setBounds(200, 70, 180, 30);
            add(categorizedByQtyButton);
            
            JButton categorizedByAmountButton = new JButton("Categorized By Amount");
            categorizedByAmountButton.setBackground(Color.BLUE);
            categorizedByAmountButton.setForeground(Color.WHITE);
			categorizedByAmountButton.setBounds(200, 110, 180, 30);
			add(categorizedByAmountButton);

			//Gray buttons
			JButton ordersByAmountButton = new JButton("Orders By Amount"); 
			ordersByAmountButton.setBackground(Color.GRAY);
            ordersByAmountButton.setForeground(Color.WHITE);
            ordersByAmountButton.setBounds(400, 70, 180, 30);
            add(ordersByAmountButton);
            
            JButton allOrdersButton = new JButton("All Orders");
            allOrdersButton.setBackground(Color.GRAY);
            allOrdersButton.setForeground(Color.WHITE);
			allOrdersButton.setBounds(400, 110, 180, 30);
			add(allOrdersButton);
			
			// Back button functionality
			backButton.addActionListener(e -> dispose());
			
			// Adding action listeners for buttons
            viewCustomerButton.addActionListener(e -> showViewCustomersReport());
            bestCustomerButton.addActionListener(e -> showBestCustomersReport());
            allCustomersButton.addActionListener(e -> showAllCustomersReport());
            categorizedByQtyButton.addActionListener(e -> showBestSellingByQuantity());
            categorizedByAmountButton.addActionListener(e -> showBestSellingByAmount());
            ordersByAmountButton.addActionListener(e -> showOrdersByAmount());
            allOrdersButton.addActionListener(e -> showAllOrders());

			setLocationRelativeTo(null);
			setVisible(true);
		}
		
		 // Method to show Best Customers Report
        private void showBestCustomersReport() {
            JFrame reportFrame = new JFrame("Best Customers Report");
            reportFrame.setSize(500, 400);
            reportFrame.setLayout(null);
            
            // Back button
            JButton backButton = new JButton("Back");
            backButton.setBounds(10, 10, 80, 30);
            backButton.setForeground(Color.WHITE);
            backButton.setBackground(new Color(255, 102, 102));
            backButton.addActionListener(e -> reportFrame.dispose()); // Close the report frame
            reportFrame.add(backButton);
            
            // Table model and setup
            String[] columns = {"Customer ID", "Total Orders", "Total Amount"};
            DefaultTableModel model = new DefaultTableModel();
            model.setColumnIdentifiers(columns);
            JTable table = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBounds(50, 50, 400, 250);
            reportFrame.add(scrollPane);
            
            Map<String, Integer> customerOrderCount = new HashMap<>();
            Map<String, Double> customerTotalAmount = new HashMap<>();
            
            // Calculate total orders and amount for each customer
            for (Order order : orderDatabase.values()) {
                double price = getPriceBySize(order.size);
                double amount = price * order.quantity;
                
                // Update total orders and amounts per customer
                customerOrderCount.put(order.customerId, customerOrderCount.getOrDefault(order.customerId, 0) + 1);
                customerTotalAmount.put(order.customerId, customerTotalAmount.getOrDefault(order.customerId, 0.0) + amount);
            }
            
            // Sort by total amount in descending order and display results
            customerTotalAmount.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .forEach(entry -> {
                String customerId = entry.getKey();
                int totalOrders = customerOrderCount.get(customerId);
                double totalAmount = entry.getValue();
                model.addRow(new Object[]{customerId, totalOrders, String.format("%.2f", totalAmount)});
            });
            
            // Make sure the frame is centered on the screen and visible
            reportFrame.setLocationRelativeTo(null);
            reportFrame.setVisible(true);
        }
        
        // Method to show View Customers Report
        private void showViewCustomersReport() {
            JFrame reportFrame = new JFrame("All Customers Report");
            reportFrame.setSize(500, 400);
            reportFrame.setLayout(null);
            
            // Back button
            JButton backButton = new JButton("Back");
            backButton.setBounds(10, 10, 80, 30);
            backButton.setForeground(Color.WHITE);
            backButton.setBackground(new Color(255, 102, 102));
            backButton.addActionListener(e -> reportFrame.dispose()); // Close the report frame
            reportFrame.add(backButton);
            
            // Table model and setup
            String[] columns = {"Customer ID", "Total Orders", "Total Amount"};
            DefaultTableModel model = new DefaultTableModel();
            model.setColumnIdentifiers(columns);
            JTable table = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBounds(50, 50, 400, 250);
            reportFrame.add(scrollPane);
            
            Map<String, Integer> customerOrderCount = new HashMap<>();
            Map<String, Double> customerTotalAmount = new HashMap<>();
            
            // Calculate total orders and amount for each customer
            for (Order order : orderDatabase.values()) {
                double price = getPriceBySize(order.size);
                double amount = price * order.quantity;
                
                // Update total orders and amounts per customer
                customerOrderCount.put(order.customerId, customerOrderCount.getOrDefault(order.customerId, 0) + 1);
                customerTotalAmount.put(order.customerId, customerTotalAmount.getOrDefault(order.customerId, 0.0) + amount);
            }
            
            // Display all customers without sorting
            customerOrderCount.forEach((customerId, totalOrders) -> {
                double totalAmount = customerTotalAmount.get(customerId);
                model.addRow(new Object[]{customerId, totalOrders, String.format("%.2f", totalAmount)});
            });
            
            // Make sure the frame is centered on the screen and visible
            reportFrame.setLocationRelativeTo(null);
            reportFrame.setVisible(true);
        }
        
        // Method to show All Customers Report with T-shirt Sizes
        private void showAllCustomersReport() {
            JFrame reportFrame = new JFrame("All Customers Report");
            reportFrame.setSize(600, 400); // Adjusted size for wider table
            reportFrame.setLayout(null);
            
            // Back button
            JButton backButton = new JButton("Back");
            backButton.setBounds(10, 10, 80, 30);
            backButton.setForeground(Color.WHITE);
            backButton.setBackground(new Color(255, 102, 102));
            backButton.addActionListener(e -> reportFrame.dispose()); // Close the report frame
            reportFrame.add(backButton);
            
            // Table model and setup with specified columns
            String[] columns = {"Customer ID", "XS", "S", "M", "L", "XL", "XXL", "Total Amount"};
            DefaultTableModel model = new DefaultTableModel();
            model.setColumnIdentifiers(columns);
            JTable table = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBounds(10, 50, 560, 300); // Adjusted bounds for scroll pane
            reportFrame.add(scrollPane);
            
            // Map to store customer amounts per size
            Map<String, double[]> customerSizeAmounts = new HashMap<>();
            
            // Initialize customer amounts with 0 for each size
            for (Order order : orderDatabase.values()) {
                double price = getPriceBySize(order.size);
                double amount = price * order.quantity;
                
                // Initialize the array if it doesn't exist
                customerSizeAmounts.putIfAbsent(order.customerId, new double[6]); // 6 sizes: XS, S, M, L, XL, XXL
                double[] sizeAmounts = customerSizeAmounts.get(order.customerId);
                
                // Update the corresponding size index
                switch (order.size) {
                    case "XS": sizeAmounts[0] += amount; break;
                    case "S": sizeAmounts[1] += amount; break;
                    case "M": sizeAmounts[2] += amount; break;
                    case "L": sizeAmounts[3] += amount; break;
                    case "XL": sizeAmounts[4] += amount; break;
                    case "XXL": sizeAmounts[5] += amount; break;
                }
            }
            
            // Populate the table with customer ID, size amounts, and total amount
            customerSizeAmounts.forEach((customerId, sizeAmounts) -> {
                double totalAmount = 0;
                for (double amount : sizeAmounts) {
                    totalAmount += amount; // Calculate total amount
                }
                model.addRow(new Object[]{
                    customerId,
                    String.format("%.2f", sizeAmounts[0]),
                    String.format("%.2f", sizeAmounts[1]),
                    String.format("%.2f", sizeAmounts[2]),
                    String.format("%.2f", sizeAmounts[3]),
                    String.format("%.2f", sizeAmounts[4]),
                    String.format("%.2f", sizeAmounts[5]),
                    String.format("%.2f", totalAmount)
                });
            });
            
            // Make sure the frame is centered on the screen and visible
            reportFrame.setLocationRelativeTo(null);
            reportFrame.setVisible(true);
        }
        
        
        // Method to show Best Selling Categories by Quantity
        private void showBestSellingByQuantity() {
            JFrame reportFrame = new JFrame("Best Selling by Quantity");
            reportFrame.setSize(500, 400);
            reportFrame.setLayout(null);
            
            // Back button
            JButton backButton = new JButton("Back");
            backButton.setBounds(10, 10, 80, 30);
            backButton.setForeground(Color.WHITE);
            backButton.setBackground(new Color(255, 102, 102));
            backButton.addActionListener(e -> reportFrame.dispose()); // Close the report frame
            reportFrame.add(backButton);
            
            // Table model and setup
            String[] columns = {"Size", "Total Quantity", "Total Amount"};
            DefaultTableModel model = new DefaultTableModel();
            model.setColumnIdentifiers(columns);
            JTable table = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBounds(50, 50, 400, 250);
            reportFrame.add(scrollPane);
            
            Map<String, Integer> sizeQuantities = new HashMap<>();
            Map<String, Double> sizeAmounts = new HashMap<>();
            
            // Calculate total quantity and amount for each size
            for (Order order : orderDatabase.values()) {
                double price = getPriceBySize(order.size);
                int quantity = order.quantity;
                double amount = price * quantity;
                
                sizeQuantities.put(order.size, sizeQuantities.getOrDefault(order.size, 0) + quantity);
                sizeAmounts.put(order.size, sizeAmounts.getOrDefault(order.size, 0.0) + amount);
            }
            
            // Sort by quantity in descending order and display results
            sizeQuantities.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .forEach(entry -> {
                String size = entry.getKey();
                int totalQuantity = entry.getValue();
                double totalAmount = sizeAmounts.get(size);
                model.addRow(new Object[]{size, totalQuantity, String.format("%.2f", totalAmount)});
            });
            
            // Make sure the frame is centered on the screen and visible
            reportFrame.setLocationRelativeTo(null);
            reportFrame.setVisible(true);
        }
        
        // Method to show Best Selling Categories by Amount
        private void showBestSellingByAmount() {
            JFrame reportFrame = new JFrame("Best Selling by Amount");
            reportFrame.setSize(500, 400);
            reportFrame.setLayout(null);
            
            // Back button
            JButton backButton = new JButton("Back");
            backButton.setBounds(10, 10, 80, 30);
            backButton.setForeground(Color.WHITE);
            backButton.setBackground(new Color(255, 102, 102));
            backButton.addActionListener(e -> reportFrame.dispose()); // Close the report frame
            reportFrame.add(backButton);
            
            // Table model and setup
            String[] columns = {"Size", "QTY", "Total Amount"};
            DefaultTableModel model = new DefaultTableModel();
            model.setColumnIdentifiers(columns);
            JTable table = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBounds(50, 50, 400, 250);
            reportFrame.add(scrollPane);
            
            Map<String, Integer> sizeQuantities = new HashMap<>();
            Map<String, Double> sizeAmounts = new HashMap<>();
            
            // Calculate total quantity and amount for each size
            for (Order order : orderDatabase.values()) {
                double price = getPriceBySize(order.size);
                int quantity = order.quantity;
                double amount = price * quantity;
                
                sizeQuantities.put(order.size, sizeQuantities.getOrDefault(order.size, 0) + quantity);
                sizeAmounts.put(order.size, sizeAmounts.getOrDefault(order.size, 0.0) + amount);
            }
            
            // Sort by total amount in descending order and display results
            sizeAmounts.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .forEach(entry -> {
                String size = entry.getKey();
                int totalQuantity = sizeQuantities.get(size);
                double totalAmount = entry.getValue();
                model.addRow(new Object[]{size, totalQuantity, String.format("%.2f", totalAmount)});
            });
            
            // Make sure the frame is centered on the screen and visible
            reportFrame.setLocationRelativeTo(null);
            reportFrame.setVisible(true);
        }
        
        // Method to show All Orders
        private void showAllOrders() {
            JFrame reportFrame = new JFrame("All Orders");
            reportFrame.setSize(600, 400);
            reportFrame.setLayout(null);
            
            // Back button
            JButton backButton = new JButton("Back");
            backButton.setBounds(10, 10, 80, 30);
            backButton.setForeground(Color.WHITE);
            backButton.setBackground(new Color(255, 102, 102));
            backButton.addActionListener(e -> reportFrame.dispose()); // Close the report frame
            reportFrame.add(backButton);
            
            // Table model and setup
            String[] columns = {"Order ID", "Customer ID", "Size", "QTY", "Amount", "Status"};
            DefaultTableModel model = new DefaultTableModel();
            model.setColumnIdentifiers(columns);
            JTable table = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBounds(50, 50, 500, 250);
            reportFrame.add(scrollPane);
            
            // Display all orders sorted by Order ID in descending order
            orderDatabase.values().stream()
            .sorted((o1, o2) -> o2.orderId.compareTo(o1.orderId))  // Sort by Order ID descending
            .forEach(order -> {
                double totalAmount = getPriceBySize(order.size) * order.quantity;
                model.addRow(new Object[]{
                    order.orderId, order.customerId, order.size, order.quantity, 
                    String.format("%.2f", totalAmount), order.status}); // Assuming order has a 'status' field
                });
                
                // Make sure the frame is centered on the screen and visible
                reportFrame.setLocationRelativeTo(null);
                reportFrame.setVisible(true);
            }
            
            
            // Method to show Orders by Amount
            private void showOrdersByAmount() {
                JFrame reportFrame = new JFrame("Orders by Amount");
                reportFrame.setSize(600, 400);
                reportFrame.setLayout(null);
                
                // Back button
                JButton backButton = new JButton("Back");
                backButton.setBounds(10, 10, 80, 30);
                backButton.setForeground(Color.WHITE);
                backButton.setBackground(new Color(255, 102, 102));
                backButton.addActionListener(e -> reportFrame.dispose()); // Close the report frame
                reportFrame.add(backButton);
                
                // Table model and setup
                String[] columns = {"Order ID", "Customer ID", "Size", "Quantity", "Total Amount", "Status"};
                DefaultTableModel model = new DefaultTableModel();
                model.setColumnIdentifiers(columns);
                JTable table = new JTable(model);
                JScrollPane scrollPane = new JScrollPane(table);
                scrollPane.setBounds(50, 50, 500, 250);
                reportFrame.add(scrollPane);
                
                // Display all orders sorted by total amount in descending order
                orderDatabase.values().stream()
                .sorted((o1, o2) -> {
                    double amount1 = getPriceBySize(o1.size) * o1.quantity;
                    double amount2 = getPriceBySize(o2.size) * o2.quantity;
                    return Double.compare(amount2, amount1);  // Sort by total amount descending
                })
                .forEach(order -> {
                    double totalAmount = getPriceBySize(order.size) * order.quantity;
                    model.addRow(new Object[]{
                        order.orderId, order.customerId, order.size, order.quantity, String.format("%.2f", totalAmount), order.status}); // Assuming order has a 'status' field
                    });
                    
                    // Make sure the frame is centered on the screen and visible
                    reportFrame.setLocationRelativeTo(null);
                    reportFrame.setVisible(true);
                }
                
                
                // Assuming this method exists in the existing code for price determination
                private double getPriceBySize(String size) {
                    switch (size) {
                        case "XS": return 600;
                        case "S": return 800;
                        case "M": return 900;
                        case "L": return 1000;
                        case "XL": return 1100;
                        case "XXL": return 1200;
                        default: return -1;
                    }
                }
          }

		// Delete Order GUI
        private static class DeleteOrderGUI extends JFrame {
                DeleteOrderGUI() {
                    setTitle("Delete Order");
                    setSize(500, 400);
                    setLayout(null);
                    
                    // Back Button
                    JButton backButton = new JButton("Back");
                    backButton.setBounds(10, 10, 80, 30);
                    backButton.setForeground(Color.WHITE);
                    backButton.setBackground(new Color(255, 102, 102));
                    backButton.addActionListener(e -> dispose());
                    add(backButton);
                    
					// Order ID Section
                    JLabel orderIdLabel = new JLabel("Enter Order ID:");
                    orderIdLabel.setBounds(50, 50, 150, 30);
                    add(orderIdLabel);
                    
                    JTextField orderIdField = new JTextField();
                    orderIdField.setBounds(200, 50, 150, 30);
                    add(orderIdField);
                    
                    JButton searchButton = new JButton("Search");
                    searchButton.setBounds(370, 50, 100, 30);
                    searchButton.setForeground(Color.WHITE);
                    searchButton.setBackground(new Color(0, 153, 153));
                    add(searchButton);
                    
                    // Labels for Order Details
                    JLabel customerIdLabel = new JLabel("Customer ID:");
                    customerIdLabel.setBounds(50, 100, 150, 30);
                    add(customerIdLabel);
                    
                    JLabel sizeLabel = new JLabel("Size:");
                    sizeLabel.setBounds(50, 130, 150, 30);
                    add(sizeLabel);
                    
                    JLabel qtyLabel = new JLabel("Quantity:");
                    qtyLabel.setBounds(50, 160, 150, 30);
                    add(qtyLabel);
                    
                    JLabel amountLabel = new JLabel("Amount:");
                    amountLabel.setBounds(50, 190, 150, 30);
                    add(amountLabel);
                    
                    JLabel statusLabel = new JLabel("Status:");
                    statusLabel.setBounds(50, 220, 150, 30);
                    add(statusLabel);
                    
                    // Delete Button
                    JButton deleteButton = new JButton("Delete");
                    deleteButton.setBounds(350, 300, 120, 30);
                    deleteButton.setForeground(Color.WHITE);
                    deleteButton.setBackground(new Color(139, 69, 19)); // Dark brown color
                    add(deleteButton);
                    
                    // Search Button Action
                    searchButton.addActionListener(e -> {
                        String orderId = orderIdField.getText();
                        if (orderDatabase.containsKey(orderId)) {
                            Order order = orderDatabase.get(orderId);
                            customerIdLabel.setText("Customer ID: " + order.customerId);
                            sizeLabel.setText("Size: " + order.size);
                            qtyLabel.setText("Quantity: " + order.quantity);
                            double totalAmount = getPriceBySize(order.size) * order.quantity;
                            amountLabel.setText("Amount: " + String.format("%.2f", totalAmount));
                            statusLabel.setText("Status: " + order.status);
                        } else {
                            JOptionPane.showMessageDialog(this, "Order not found!");
                        }
                    });
                    
                    // Delete Button Action
                    deleteButton.addActionListener(e -> {
                        String orderId = orderIdField.getText();
                        if (orderDatabase.containsKey(orderId)) {
                            int confirmation = JOptionPane.showConfirmDialog(this,
                            "Are you sure you want to delete this order?", "Confirm Deletion",
                            JOptionPane.YES_NO_OPTION);
                            if (confirmation == JOptionPane.YES_OPTION) {
                                orderDatabase.remove(orderId);
                                JOptionPane.showMessageDialog(this, "Order deleted.");
                                // Clear the labels after deletion
                                customerIdLabel.setText("Customer ID:");
                                sizeLabel.setText("Size:");
                                qtyLabel.setText("Quantity:");
                                amountLabel.setText("Amount:");
                                statusLabel.setText("Status:");
                            }
                        } else {
                            JOptionPane.showMessageDialog(this, "Order not found!");
                        }
                    });
                    
                    setLocationRelativeTo(null);
                    setVisible(true);
                }
                private double getPriceBySize(String size) {
                    switch (size) {
                        case "XS": return 600;
                        case "S": return 800;
                        case "M": return 900;
                        case "L": return 1000;
                        case "XL": return 1100;
                        case "XXL": return 1200;
                        default: return -1; // or throw an exception if size is invalid
                    }
                }
           }

    // Make PlaceOrderGUI a static inner class
    public static class PlaceOrderGUI extends JFrame {
        private static int orderCount = 1; // Initial Order Count

        public PlaceOrderGUI() {
            setTitle("Place Order");
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setSize(490, 400);
            setLayout(null);

            String orderIdString = String.format("ODR#%06d", orderCount);

            JLabel orderIdLabel = new JLabel("Order ID :");
            orderIdLabel.setBounds(30, 80, 100, 30);
            add(orderIdLabel);

            JLabel orderIdValue = new JLabel(orderIdString);
            orderIdValue.setBounds(150, 80, 100, 30);
            add(orderIdValue);

            JLabel customerIdLabel = new JLabel("Customer ID :");
            customerIdLabel.setBounds(30, 130, 100, 30);
            add(customerIdLabel);

            JTextField customerIdField = new JTextField();
            customerIdField.setBounds(150, 130, 200, 30);
            add(customerIdField);

            JLabel sizeLabel = new JLabel("Size :");
            sizeLabel.setBounds(30, 180, 100, 30);
            add(sizeLabel);

            JTextField sizeField = new JTextField();
            sizeField.setBounds(150, 180, 200, 30);
            add(sizeField);

            JLabel sizeOptionsLabel = new JLabel("(XS/S/M/L/XL/XXL)");
            sizeOptionsLabel.setBounds(360, 180, 150, 30);
            add(sizeOptionsLabel);

            JLabel qtyLabel = new JLabel("QTY :");
            qtyLabel.setBounds(30, 230, 100, 30);
            add(qtyLabel);

            JTextField qtyField = new JTextField();
            qtyField.setBounds(150, 230, 200, 30);
            add(qtyField);

            JLabel amountLabel = new JLabel("Amount :");
            amountLabel.setBounds(30, 280, 100, 30);
            add(amountLabel);

            JLabel amountValue = new JLabel("");
            amountValue.setBounds(150, 280, 200, 30);
            add(amountValue);

            JButton backButton = new JButton("Back");
            backButton.setBounds(20, 20, 80, 30);
            backButton.setBackground(new Color(255, 102, 102));
            backButton.setForeground(Color.WHITE);
            add(backButton);

            JButton placeButton = new JButton("Place");
            placeButton.setBounds(360, 280, 100, 30);
            placeButton.setBackground(new Color(0, 153, 153));
            placeButton.setForeground(Color.WHITE);
            add(placeButton);

            
            double[] prices = {600.00, 800.00, 900.00, 1000.00, 1100.00, 1200.00};
            String[] sizes = {"XS", "S", "M", "L", "XL", "XXL"};

            placeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String customerId = customerIdField.getText();
                    if (customerId.length() != 10 || !customerId.startsWith("0")) {
                        JOptionPane.showMessageDialog(PlaceOrderGUI.this, "Invalid Input! (Customer ID must be 10 digits starting with 0)");
                        return;
                    }

                    String size = sizeField.getText().toUpperCase();
                    int sizeIndex = -1;
                    for (int i = 0; i < sizes.length; i++) {
                        if (sizes[i].equals(size)) {
                            sizeIndex = i;
                            break;
                        }
                    }
                    if (sizeIndex == -1) {
                        JOptionPane.showMessageDialog(PlaceOrderGUI.this, "Invalid Input: Size must be XS, S, M, L, XL, or XXL.");
                        return;
                    }

                    int qty;
                    try {
                        qty = Integer.parseInt(qtyField.getText());
                        if (qty <= 0) {
                            throw new NumberFormatException();
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(PlaceOrderGUI.this, "Invalid Input! (Quantity must be a positive integer)");
                        return;
                    }

					double totalAmount = getPriceBySize(size) * qty; // Calculate amount
					amountValue.setText(String.format("%.2f", totalAmount));
					
					Order newOrder = new Order(orderIdString, customerId, size, qty, totalAmount, OrderStatus.PROCESSING.toString());

					FashionShopGUI.orders.add(newOrder);// Add the new order to the orders list

                    orderCount++;
                    String nextOrderId = String.format("ODR#%06d", orderCount);
                    orderIdValue.setText(nextOrderId);

					JOptionPane.showMessageDialog(PlaceOrderGUI.this, "Order placed successfully!", "Order Status", JOptionPane.INFORMATION_MESSAGE);

					customerIdField.setText("");
					sizeField.setText("");
					qtyField.setText("");
					amountValue.setText("");

                }
            });

            backButton.addActionListener(e -> dispose()); // Close the Place Order window

            setLocationRelativeTo(null);
            setVisible(true);
        }
    }

		class Customer {
			String customerId;

			Customer(String customerId) {
				this.customerId = customerId;
			}
		}

		static class Order {
			String orderId;
			String customerId;
			String size;
			int quantity;
			double amount; 
			String status;

			Order(String orderId, String customerId, String size, int quantity, double amount, String status) {
				this.orderId = orderId;
				this.customerId = customerId;
				this.size = size;
				this.quantity = quantity;
				this.amount = amount; 
				this.status = status;
			}
		}
}
		

