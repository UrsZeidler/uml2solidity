package de.urszeidler.eclipse.solidity.compiler.support.preferences;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.urszeidler.eclipse.solidity.compiler.support.Activator;
import de.urszeidler.eclipse.solidity.compiler.support.preferences.PreferenceConstants.SolC;
import de.urszeidler.eclipse.solidity.compiler.support.util.StartCompiler;

public class InstalledSolidityCompilerPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
	private static final String SOLC_VERSION_PATTERN = "((.*)Version:[ ]*(.*)$)";

	private static class ContentProvider implements IStructuredContentProvider {
		public Object[] getElements(Object inputElement) {
			return new Object[0];
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	private class TableLabelProvider extends LabelProvider implements ITableLabelProvider {
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			return element.toString();
		}
	}

	private Table fTable;
	private TableViewer fCompilerList;
	private Button fAddButton;
	private Button fRemoveButton;
	private Button fSearchButton;
	// private List<String> installedSolidityCompilers;
	private List<SolC> installedSolCs;

	/**
	 * Create the preference page.
	 */
	public InstalledSolidityCompilerPreferencePage() {
		super();
		setDescription("Add/remove or search solidity compilers useable with the solidity builder.");

	}

	/**
	 * Create contents of the preference page.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createContents(Composite parent) {
		IPreferenceStore store = getPreferenceStore();
		String isc = store.getString(PreferenceConstants.INSTALLED_SOL_COMPILERS);
		installedSolCs = PreferenceConstants.parsePreferences(isc);

		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout(3, false));

		Font font = parent.getFont();

		fTable = new Table(container, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 250;
		gd.widthHint = 350;
		fTable.setLayoutData(gd);
		fTable.setFont(font);
		fTable.setHeaderVisible(true);
		fTable.setLinesVisible(true);

		TableColumn column = new TableColumn(fTable, SWT.NULL);
		column.setText("Compiler");
		column.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fCompilerList.refresh(true);
			}
		});
		int defaultwidth = 350 / 3 + 1;
		column.setWidth(defaultwidth);

		column = new TableColumn(fTable, SWT.NULL);
		column.setText("version");
		column.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fCompilerList.refresh(true);
			}
		});
		column.setWidth(defaultwidth);

		column = new TableColumn(fTable, SWT.NULL);
		column.setText("path");
		column.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fCompilerList.refresh(true);
			}
		});
		column.setWidth(defaultwidth);

		fCompilerList = new TableViewer(fTable);
		fCompilerList.setUseHashlookup(true);
		fCompilerList.setContentProvider(new ContentProvider() {
			@Override
			public Object[] getElements(Object inputElement) {
				return installedSolCs.toArray();
			}
		});
		fCompilerList.setLabelProvider(new TableLabelProvider() {
			@Override
			public String getColumnText(Object element, int columnIndex) {
				if (element instanceof SolC) {
					SolC solc = (SolC) element;
					switch (columnIndex) {
					case 0:
						return solc.name;
					case 1:
						return solc.version;
					case 2:
						return solc.path;

					}
				}
				return super.getColumnText(element, columnIndex);
			}
		});

		Composite buttons = new Composite(container, SWT.NULL);
		buttons.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));
		buttons.setLayout(new GridLayout(1, false));

		fAddButton = new Button(buttons, SWT.PUSH);
		fAddButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		fAddButton.setText("add");
		fAddButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event evt) {
				addCompiler();
			}
		});

		fRemoveButton = new Button(buttons, SWT.PUSH);
		fRemoveButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		fRemoveButton.setText("remove");
		fRemoveButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event evt) {
				removeCompilers();
			}
		});

		fSearchButton = new Button(buttons, SWT.PUSH);
		fSearchButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		fSearchButton.setText("search");
		fSearchButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event evt) {
				search();
			}
		});
		fAddButton.setEnabled(installedSolCs.size() > 0);
		new Label(container, SWT.NONE);

		fCompilerList.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent evt) {
				enableButtons();
			}
		});

		fTable.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent event) {
				if (event.character == SWT.DEL && event.stateMask == 0) {
					if (fRemoveButton.isEnabled()) {
						removeCompilers();
					}
				}
			}
		});

		enableButtons();
		fCompilerList.setInput(installedSolCs.toArray());

		return container;
	}

	protected void enableButtons() {
		fAddButton.setEnabled(true);
		fRemoveButton.setEnabled(!fCompilerList.getSelection().isEmpty());

	}

	protected void addCompiler() {
		FileDialog fileDialog = new FileDialog(getShell());
		fileDialog.setFileName("solc");
		fileDialog.setText("select solc ");
		String open = fileDialog.open();

		File file = new File(open);
		SolC testSolCFile = testSolCFile(file);
		if (testSolCFile != null) {
			installedSolCs.add(testSolCFile);
			fCompilerList.setInput(installedSolCs);
		}

	}

	private SolC testSolCFile(File file) {
		final SolC solC = PreferenceConstants.solCof(file.getName(), file.getAbsolutePath(), null);
		if (file != null && file.canExecute() && file.getName().toLowerCase().equals("solc")) {
			List<String> options = new ArrayList<String>();
			options.add(file.getAbsolutePath());
			options.add("--version");

			final Pattern compile2 = Pattern.compile(SOLC_VERSION_PATTERN, Pattern.MULTILINE | Pattern.DOTALL);
			StartCompiler.startCompiler(Collections.<String>emptyList(), new StartCompiler.CompilerCallback() {

				@Override
				public void compiled(String input, String error, Exception exception) {
					if (exception != null)
						Activator.logError(error, exception);
					System.out.println(input);
					if (input != null) {
						Matcher matcher = compile2.matcher(input);
						if (matcher.matches()) {
							solC.version = matcher.group(3).trim();
						}
					}
				}
			}, options);
			if (solC.version != null) {
				solC.name = solC.name + "-" + solC.version;
				return solC;
			}

		}
		return null;
	}

	protected void removeCompilers() {
		IStructuredSelection structuredSelection = fCompilerList.getStructuredSelection();
		if (installedSolCs.remove(structuredSelection.getFirstElement()))
			fCompilerList.setInput(installedSolCs.toArray());
	}

	protected void search() {
		DirectoryDialog directoryDialog = new DirectoryDialog(getShell());
		directoryDialog.setText("search solc ");
		String open = directoryDialog.open();

		final File file = new File(open);
		final Set<SolC> list = new HashSet<SolC>();

		IRunnableWithProgress withProgress = new IRunnableWithProgress() {

			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				monitor.beginTask("file:" + file.getAbsolutePath(), IProgressMonitor.UNKNOWN);
				searchAndAdd(file, list, monitor);
				monitor.done();
			}
		};

		try {
			new ProgressMonitorDialog(getShell()).run(true, true, withProgress);
			installedSolCs.addAll(list);
			fCompilerList.setInput(installedSolCs.toArray());
		} catch (InvocationTargetException | InterruptedException e) {
			Activator.logError("", e);
		}

	}

	/**
	 * @param file
	 * @param list
	 * @param monitor
	 */
	private void searchAndAdd(File file, Set<SolC> list, IProgressMonitor monitor) {
		if(file==null || monitor.isCanceled())
			return;
			
		testAndAdd(list, file);
		monitor.setTaskName("Searching: found("+list.size()+") "+ file.getAbsolutePath());
		File[] listFiles = file.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return dir.isDirectory();
			}
		});
		if (listFiles != null)
			for (File file2 : listFiles) {
				searchAndAdd(file2, list, monitor);
			}
	}

	/**
	 * @param list
	 * @param file2
	 */
	private void testAndAdd(Set<SolC> list, File file2) {
		if (file2 == null)
			return;
		
		File[] listFiles2 = file2.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return "solc".equals(name);
			}
		});
		if (listFiles2 != null && listFiles2.length == 1) {
			SolC testSolCFile = testSolCFile(listFiles2[0]);
			if (testSolCFile != null)
				list.add(testSolCFile);
		}
	}

	/**
	 * Initialize the preference page.
	 */
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}

	@Override
	protected void performApply() {
		StringBuffer buffer = new StringBuffer();
		for (Iterator<SolC> iterator = installedSolCs.iterator(); iterator.hasNext();) {
			SolC solC = (SolC) iterator.next();
			buffer.append(solC.name).append(",").append(solC.path).append(",").append(solC.version);
			if (iterator.hasNext())
				buffer.append("::");
		}
		getPreferenceStore().setValue(PreferenceConstants.INSTALLED_SOL_COMPILERS, buffer.toString());
		super.performApply();
	}

}
