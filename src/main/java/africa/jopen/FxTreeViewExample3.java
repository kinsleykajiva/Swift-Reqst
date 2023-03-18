package africa.jopen;

import java.util.ArrayList;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView.EditEvent;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.control.TreeView;
import javafx.event.EventHandler;

public class FxTreeViewExample3 extends Application
{
	// Create the TextArea
	private final TextArea textArea = new TextArea();

	public static void main(String[] args)
	{
		Application.launch(args);
	}

	@Override
	public void start(Stage stage)
	{
		// Create the TreeViewHelper
		TreeViewHelper helper = new TreeViewHelper();
		// Get the Products
		//ArrayList<TreeItem<String>> products = helper.getProducts();

		// Create the TreeView
		TreeView<String> treeView = new TreeView<>();
		// Create the Root TreeItem
		TreeItem<String> rootItem = new TreeItem<String>("Vehicles");
		// Add children to the root
	//	rootItem.getChildren().addAll(products);
		// Set the Root Node
		treeView.setRoot(rootItem);
		// Make the TreeView editable
		treeView.setEditable(true);
		// Set a cell factory to use TextFieldTreeCell
		treeView.setCellFactory(TextFieldTreeCell.forTreeView());

		// Set editing related event handlers (OnEditStart)
		treeView.setOnEditStart(new EventHandler<EditEvent<String>>()
		{
			@Override
			public void handle(EditEvent<String> event)
			{
				editStart(event);
			}
		});

		// Set editing related event handlers (OnEditCommit)
		treeView.setOnEditCommit(new EventHandler<EditEvent<String>>()
		{
			@Override
			public void handle(EditEvent<String> event)
			{
				editCommit(event);
			}
		});

		// Set editing related event handlers (OnEditCancel)
		treeView.setOnEditCancel(new EventHandler<EditEvent<String>>()
		{
			@Override
			public void handle(EditEvent<String> event)
			{
				editCancel(event);
			}
		});

		// Set the preferred number of text rows
		textArea.setPrefRowCount(15);
		// Set the preferred number of text columns
		textArea.setPrefColumnCount(25);

		// Create the VBox
		VBox root = new VBox();
		// Add the TreeView to the VBox
		root.getChildren().addAll(treeView,new Label("Message Log:"), textArea);
		// Create the Scene
		Scene scene = new Scene(root,400,500);
		// Add the Scene to the Stage
		stage.setScene(scene);
		// Set the Title for the Scene
		stage.setTitle("TreeView Example 3");
		// Display the stage
		stage.show();
	}

	// Helper Methods for the Event Handlers
	private void editStart(EditEvent<String> event)
	{
		writeMessage("Started editing: " + event.getTreeItem() );
	}

	private void editCommit(EditEvent<String> event)
	{
		writeMessage(event.getTreeItem() + " changed." +
				" old = " + event.getOldValue() +
				", new = " + event.getNewValue());
	}

	private void editCancel(EditEvent<String> e)
	{
		writeMessage("Cancelled editing: " + e.getTreeItem() );
	}

	// Method for Logging
	private void writeMessage(String msg)
	{
		this.textArea.appendText(msg + "\n");
	}

}
