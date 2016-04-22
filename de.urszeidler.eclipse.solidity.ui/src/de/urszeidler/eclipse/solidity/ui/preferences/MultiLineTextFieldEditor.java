package de.urszeidler.eclipse.solidity.ui.preferences;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Taken from here : https://www.eclipse.org/forums/index.php/t/159671/
 * @author uzeidler
 *
 */
public class MultiLineTextFieldEditor extends FieldEditor {

	public static final int VALIDATE_ON_KEY_STROKE = 0;
	public static final int VALIDATE_ON_FOCUS_LOST = 1;
	public static int UNLIMITED = -1;

	private boolean isValid;
	private String oldValue;
	private String compTitle;
	private Label title;
	private Text textField;
	private int widthInChars = UNLIMITED;
	private int textLimit = UNLIMITED;
	private String errorMessage;
	private boolean emptyStringAllowed = true;
	private int validateStrategy = VALIDATE_ON_KEY_STROKE;

	protected MultiLineTextFieldEditor() {
	}

	public MultiLineTextFieldEditor(String name, String labelText, int width,
			int strategy, Composite parent) {
		init(name, labelText);
		widthInChars = width;
		setValidateStrategy(strategy);
		isValid = false;
		errorMessage = "error";
		createControl(parent);
	}

	public MultiLineTextFieldEditor(String name, String labelText, int width,
			Composite parent) {
		this(name, labelText, width, VALIDATE_ON_KEY_STROKE, parent);
		this.compTitle = labelText;
	}

	public MultiLineTextFieldEditor(String name, String labelText,
			Composite parent) {
		this(name, labelText, UNLIMITED, parent);
	}

	@Override
	protected void adjustForNumColumns(int numColumns) {
		GridData gd = (GridData) textField.getLayoutData();
		gd.horizontalSpan = numColumns - 1;
		// We only grab excess space if we have to
		// If another field editor has more columns then
		// we assume it is setting the width.
		gd.grabExcessHorizontalSpace = gd.horizontalSpan == 1;
	}

	protected boolean checkState() {
		boolean result = false;
		if (emptyStringAllowed)
			result = true;

		if (textField == null)
			result = false;

		String txt = textField.getText();

		if (txt == null)
			result = false;

		result = (txt.trim().length() > 0) || emptyStringAllowed;

		// call hook for subclasses
		result = result && doCheckState();

		if (result)
			clearErrorMessage();
		else
			showErrorMessage(errorMessage);

		return result;
	}

	protected boolean doCheckState() {
		return true;
	}

	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {

		title = new Label(parent, SWT.UP);
		title.setFont(parent.getFont());
		this.compTitle = getLabelText();
		title.setText(this.compTitle);
		title.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

		textField = getTextControl(parent);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint = 100;
		gd.heightHint = 70;
		textField.setLayoutData(gd);

	}

	@Override
	protected void doLoad() {
		if (textField != null) {
			String value = getPreferenceStore().getString(getPreferenceName());
			textField.setText(value);
			oldValue = value;
		}
	}

	@Override
	protected void doLoadDefault() {
		if (textField != null) {
			String value = getPreferenceStore().getDefaultString(
					getPreferenceName());
			textField.setText(value);
		}
		valueChanged();
	}

	@Override
	protected void doStore() {
		getPreferenceStore().setValue(getPreferenceName(), textField.getText());
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	@Override
	public int getNumberOfControls() {
		return 2;
	}

	public String getStringValue() {
		if (textField != null)
			return textField.getText();
		else
			return getPreferenceStore().getString(getPreferenceName());
	}

	protected Text getTextControl() {
		return textField;
	}

	public Text getTextControl(Composite parent) {
		if (textField == null) {
			textField = new Text(parent, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER
					| SWT.WRAP);
			textField.setFont(parent.getFont());
			switch (validateStrategy) {
			case VALIDATE_ON_KEY_STROKE:
				textField.addKeyListener(new KeyAdapter() {
					@Override
					public void keyPressed(KeyEvent e) {
						valueChanged();
					}
				});

				textField.addFocusListener(new FocusAdapter() {
					@Override
					public void focusGained(FocusEvent e) {
						refreshValidState();
					}

					@Override
					public void focusLost(FocusEvent e) {
						clearErrorMessage();
					}
				});
				break;
			case VALIDATE_ON_FOCUS_LOST:
				textField.addKeyListener(new KeyAdapter() {
					@Override
					public void keyPressed(KeyEvent e) {
						clearErrorMessage();
					}
				});
				textField.addFocusListener(new FocusAdapter() {
					@Override
					public void focusGained(FocusEvent e) {
						refreshValidState();
					}

					@Override
					public void focusLost(FocusEvent e) {
						valueChanged();
						clearErrorMessage();
					}
				});
				break;
			default:
				Assert.isTrue(false, "Unknown validate strategy"); //$NON-NLS-1$
			}
			textField.addDisposeListener(new DisposeListener() {
//				@Override
				public void widgetDisposed(DisposeEvent event) {
					textField = null;
				}
			});
			if (textLimit > 0) { // Only set limits above 0 - see SWT spec
				textField.setTextLimit(textLimit);
			}
		} else {
			checkParent(textField, parent);
		}
		return textField;
	}

	public boolean isEmptyStringAllowed() {
		return emptyStringAllowed;
	}

	@Override
	public boolean isValid() {
		return isValid;
	}

	@Override
	protected void refreshValidState() {
		isValid = checkState();
	}

	public void setEmptyStringAllowed(boolean b) {
		emptyStringAllowed = b;
	}

	public void setErrorMessage(String message) {
		errorMessage = message;
	}

	@Override
	public void setFocus() {
		if (textField != null) {
			textField.setFocus();
		}
	}

	public void setStringValue(String value) {
		if (textField != null) {
			if (value == null)
				value = ""; //$NON-NLS-1$
			oldValue = textField.getText();
			if (!oldValue.equals(value)) {
				textField.setText(value);
				valueChanged();
			}
		}
	}

	public void setTextLimit(int limit) {
		textLimit = limit;
		if (textField != null)
			textField.setTextLimit(limit);
	}

	public void setValidateStrategy(int value) {
		Assert.isTrue(value == VALIDATE_ON_FOCUS_LOST
				|| value == VALIDATE_ON_KEY_STROKE);
		validateStrategy = value;
	}

	public void showErrorMessage() {
		showErrorMessage(errorMessage);
	}

	protected void valueChanged() {
		setPresentsDefaultValue(false);
		boolean oldState = isValid;
		refreshValidState();

		if (isValid != oldState)
			fireStateChanged(IS_VALID, oldState, isValid);

		String newValue = textField.getText();
		if (!newValue.equals(oldValue)) {
			fireValueChanged(VALUE, oldValue, newValue);
			oldValue = newValue;
		}
	}

}
