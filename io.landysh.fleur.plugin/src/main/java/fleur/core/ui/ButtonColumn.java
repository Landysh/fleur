package fleur.core.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

@SuppressWarnings("serial")
public class ButtonColumn extends AbstractCellEditor
    implements TableCellRenderer, TableCellEditor, ActionListener, MouseListener {
  private static final String BUTTON_LABEL = "X";
  private JTable table;
  private transient Action action;
  private int mnemonic;
  private transient Border originalBorder;
  private transient Border focusBorder;

  private JButton renderButton;
  private JButton editButton;
  private transient Object editorValue;
  private boolean isButtonColumnEditor;

  /**
   * Create the ButtonColumn to be used as a renderer and editor. The renderer and editor will
   * automatically be installed on the TableColumn of the specified column.
   *
   * @param table the table containing the button renderer/editor
   * @param action the Action to be invoked when the button is invoked
   * @param column the column to which the button renderer/editor is added
   */
  public ButtonColumn(JTable table, Action action, int column) {
    this.table = table;
    this.action = action;

    renderButton = new JButton();
    editButton = new JButton();
    editButton.setFocusPainted(false);
    editButton.addActionListener(this);
    originalBorder = editButton.getBorder();
    setFocusBorder(new LineBorder(Color.BLUE));

    TableColumnModel columnModel = table.getColumnModel();
    columnModel.getColumn(column).setCellRenderer(this);
    columnModel.getColumn(column).setCellEditor(this);
    table.addMouseListener(this);
  }


  /**
   * Get foreground color of the button when the cell has focus
   *
   * @return the foreground color
   */
  public Border getFocusBorder() {
    return focusBorder;
  }

  /**
   * The foreground color of the button when the cell has focus
   *
   * @param focusBorder the foreground color
   */
  public void setFocusBorder(Border focusBorder) {
    this.focusBorder = focusBorder;
    editButton.setBorder(focusBorder);
  }

  public int getMnemonic() {
    return mnemonic;
  }

  /**
   * The mnemonic to activate the button when the cell has focus
   *
   * @param mnemonic the mnemonic
   */
  public void setMnemonic(int mnemonic) {
    this.mnemonic = mnemonic;
    renderButton.setMnemonic(mnemonic);
    editButton.setMnemonic(mnemonic);
  }

  @Override
  public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
      int row, int column) {
    if (value == null) {
      editButton.setText("");
      editButton.setIcon(null);
    } else if (value instanceof Icon) {
      editButton.setText("");
      editButton.setIcon((Icon) value);
    } else {
      editButton.setText(BUTTON_LABEL);
      editButton.setIcon(null);
    }

    this.editorValue = value;
    return editButton;
  }

  @Override
  public Object getCellEditorValue() {
    return editorValue;
  }

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
      boolean hasFocus, int row, int column) {
    if (isSelected) {
      renderButton.setForeground(table.getSelectionForeground());
      renderButton.setBackground(table.getSelectionBackground());
    } else {
      renderButton.setForeground(table.getForeground());
      renderButton.setBackground(UIManager.getColor("Button.background"));
    }

    if (hasFocus) {
      renderButton.setBorder(focusBorder);
    } else {
      renderButton.setBorder(originalBorder);
    }

    if (value == null) {
      renderButton.setText("");
      renderButton.setIcon(null);
    } else if (value instanceof Icon) {
      renderButton.setText("");
      renderButton.setIcon((Icon) value);
    } else {
      renderButton.setText(BUTTON_LABEL);
      renderButton.setIcon(null);
    }

    return renderButton;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    int row = table.convertRowIndexToModel(table.getEditingRow());
    fireEditingStopped();

    ActionEvent event = new ActionEvent(table, ActionEvent.ACTION_PERFORMED, "" + Integer.toString(row));
    action.actionPerformed(event);
  }

  @Override
  public void mousePressed(MouseEvent e) {
    if (table.isEditing() && table.getCellEditor() == this)
      isButtonColumnEditor = true;
  }
  
  @Override
  public void mouseReleased(MouseEvent e) {
    if (isButtonColumnEditor && table.isEditing())
      table.getCellEditor().stopCellEditing();
    isButtonColumnEditor = false;
  }
  
  @Override
  public void mouseClicked(MouseEvent e) {/*noop*/}
  @Override
  public void mouseEntered(MouseEvent e) {/*noop*/}
  @Override
  public void mouseExited(MouseEvent e) {/*noop*/}
}
