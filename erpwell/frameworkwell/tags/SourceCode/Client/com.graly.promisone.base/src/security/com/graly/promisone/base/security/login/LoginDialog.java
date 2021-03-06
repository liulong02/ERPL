package com.graly.promisone.base.security.login;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Display;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.graphics.*;
import org.eclipse.ui.PlatformUI;

import com.graly.promisone.activeentity.client.ADManager;
import com.graly.promisone.activeentity.model.ADRefList;
import com.graly.promisone.activeentity.model.ADTable;
import com.graly.promisone.base.application.ApplicationWorkbenchAdvisor;
import com.graly.promisone.base.security.password.PasswordDialog;
import com.graly.promisone.base.ui.util.Env;
import com.graly.promisone.base.ui.util.PropertyUtil;
import com.graly.promisone.base.ui.util.SWTResourceCache;
import com.graly.promisone.base.ui.util.Message;
import com.graly.promisone.core.exception.ClientException;
import com.graly.promisone.runtime.Framework;
import com.graly.promisone.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.promisone.security.client.SecurityManager;
import com.graly.promisone.security.model.ADOrg;
import com.graly.promisone.security.model.ADUser;

public class LoginDialog {
	
	private static final Logger logger = Logger.getLogger(LoginDialog.class);
	public static final int OK = 0;
	public static final int CANCEL = 1;
	private int returnCode = OK;
	private static String ORGS = "orgs";
	private static String IS_SHOWLAUNCHER = "isShowLauncher";
	
	private Region region;
	private Shell shell;
	private CLabel cblInfo;
	private Text txtUsername = null;
	private Text txtPassword = null;
	private Combo comboLanguage = null;
	private Text txtSiteID = null;
	private Display display;
	
	private GridData detailsAreaGridData = null;

	public LoginDialog(Display display) {
		this.display = display;
	}
	
	public void createContents() {
		shell = new Shell(display, SWT.NO_TRIM | SWT.ON_TOP);
		shell.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
		final FillLayout fillLayout = new FillLayout();
		fillLayout.marginHeight = 0;
		shell.setLayout(fillLayout);
		
		final Composite composite = new Composite(shell, SWT.NONE);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.marginHeight = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.horizontalSpacing = 0;
		composite.setLayout(gridLayout);
        
		final Label lblImage = setImage(composite);
		final Composite cmpLogin = new Composite(composite, SWT.NONE);
		cmpLogin.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		final RowLayout rowLayout = new RowLayout();
		rowLayout.fill = true;
		rowLayout.marginTop = 50;
		rowLayout.spacing = 10;
		cmpLogin.setLayout(rowLayout);
		final GridData gridData = new GridData(GridData.FILL, GridData.FILL, false, false);
		gridData.widthHint = 246;
		cmpLogin.setLayoutData(gridData);
		
		createInfoLabel(cmpLogin);
		createUserNameLabel(cmpLogin);	
	    createUserNameText(cmpLogin);	
	    createPwdLabel(cmpLogin);
	    createPwdText(cmpLogin);
	    createLanguageLabel(cmpLogin);
	    createLanguageCombo(cmpLogin);
	    
		final Composite cmpButtonBar = new Composite(cmpLogin, SWT.NONE);
		final RowData rowData_5 = new RowData();
		rowData_5.height = 25;
		rowData_5.width = 230;
		cmpButtonBar.setLayoutData(rowData_5);
		cmpButtonBar.setLayout(new FormLayout());
		cmpButtonBar.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
    
	    final Button btnLogin = createLoginButton(cmpButtonBar);
	    btnLogin.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				if(checkLogin()) {
					try {
						if (confirmEntryPage(Display.getCurrent())) {
							Login.doAuthority(Env.getOrgId(), Env.getUserId());
							setReturnCode(OK);
							return;
						}
					} catch(Exception en) {
						logger.error("LoginDialog : confirmEntryPage() : " + e);
					}
				}
				setReturnCode(CANCEL);
			}
		});
	    final Button btnCancel = createCancelButton(cmpButtonBar);
	    btnCancel.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				setReturnCode(CANCEL);
				close();
			}
		});
	    
		final CLabel cblMessage = new CLabel(cmpLogin, SWT.NONE);
		cblMessage.setAlignment(SWT.RIGHT);
		cblMessage.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		final RowData rowData_6 = new RowData();
		rowData_6.height = 13;
		rowData_6.width = 235;
		cblMessage.setLayoutData(rowData_6);
		cblMessage.setText("MESwell Version:1.0");
		
		final CLabel cblMessage1 = new CLabel(cmpLogin, SWT.NONE);
		cblMessage1.setAlignment(SWT.RIGHT);
		cblMessage1.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		final RowData rowData_7 = new RowData();
		rowData_7.height = 15;
		rowData_7.width = 235;
		cblMessage1.setLayoutData(rowData_6);
		cblMessage1.setText("Copyright@2007-2009 Gralysoft");
		
		region = new Region();
		Rectangle pixel = new Rectangle(1, 1, 497, 245);
		region.add(pixel); 
		shell.setRegion(region);
		//addDragListener(composite, lblImage, shell);
		int imageX = (display.getBounds().width-pixel.width)/2;
		int imageY = (display.getBounds().height-pixel.height)/2;
		shell.setLocation(imageX, imageY);

		shell.open();
		txtUsername.setFocus();
		while (shell != null && !shell.isDisposed()) {
			try {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
	
	private Label setImage(Composite composite){
		Label lblImage = new Label(composite, SWT.NONE);
		composite.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		lblImage.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		lblImage.setImage(SWTResourceCache.getImage("login-dialog"));
		lblImage.setLayoutData(new GridData(245, 246));
		return lblImage;
    }
	
	private void createInfoLabel(Composite cmpLogin){
		cblInfo = new CLabel(cmpLogin, SWT.NONE);
		final RowData rowData = new RowData();
		rowData.width = 258;
		cblInfo.setLayoutData(rowData);
		cblInfo.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		cblInfo.setText("");
	}
	
	private void createUserNameLabel(Composite cmpLogin){
		final CLabel cblUsername = new CLabel(cmpLogin, SWT.NONE);
		final RowData rowData_1 = new RowData();
		rowData_1.width = 70;
		cblUsername.setLayoutData(rowData_1);
		cblUsername.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		cblUsername.setText("Username");
	}
	
	private void createUserNameText(Composite cmpLogin) {
		txtUsername = new Text(cmpLogin, SWT.BORDER);
		final RowData rowData_2 = new RowData();
		rowData_2.width = 150;
		txtUsername.setLayoutData(rowData_2);
	}

	private void createPwdLabel(Composite cmpLogin) {
		final CLabel cblPassword = new CLabel(cmpLogin, SWT.NONE);
		final RowData rowData_3 = new RowData();
		rowData_3.width = 70;
		cblPassword.setLayoutData(rowData_3);
		cblPassword.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		cblPassword.setText("Password");
	}

    
	private void createPwdText(Composite cmp_Login)	{
		txtPassword = new Text(cmp_Login, SWT.BORDER);
		final RowData rowData_4 = new RowData();
		rowData_4.width = 150;
		txtPassword.setLayoutData(rowData_4);
		txtPassword.setEchoChar('*');
	}
	
	private void createLanguageLabel(Composite cmpLogin) {
		final CLabel cblLanguage = new CLabel(cmpLogin, SWT.NONE);
		final RowData rowData_5 = new RowData();
		rowData_5.width = 70;
		cblLanguage.setLayoutData(rowData_5);
		cblLanguage.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		cblLanguage.setText("Language");
	}

    
	private void createLanguageCombo(Composite cmp_Login)	{
		comboLanguage = new Combo(cmp_Login, SWT.READ_ONLY);
		final RowData rowData_6 = new RowData();
		rowData_6.width = 130;
		comboLanguage.setLayoutData(rowData_6);
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			List<ADRefList> list = entityManager.getADRefList(Env.getOrgId(), "Language");
			final LinkedHashMap<String, String> languages = new LinkedHashMap<String, String>();
			languages.put("", "");
			for(ADRefList li : list){
				languages.put(li.getValue(), li.getName());
			}
			comboLanguage.setItems(languages.values().toArray(new String[]{}));
			comboLanguage.addSelectionListener(new SelectionListener() {
        	public void widgetDefaultSelected(SelectionEvent e) {
                setSelectedValue(
                        ((Combo)e.widget).getSelectionIndex());
            }
        
            public void widgetSelected(SelectionEvent e) {
                setSelectedValue(
                        ((Combo)e.widget).getSelectionIndex());
            } 
        
            private void setSelectedValue(int index) {
            	int i = 0; 
            	for (Entry<String, String> entry : languages.entrySet()){            		
            		if (i == index){
            			comboLanguage.setData(entry.getKey());
            			break;
            		}
            		i++;
            	}
            }
        });
		comboLanguage.select(0);
		} catch (Exception e) {
			logger.error("LoginDialog : createLanguageCombo()",e);
		}
	}	
	
	private Button createLoginButton(Composite cmpButtonBar){
		final Button btnLogin = new Button(cmpButtonBar, SWT.FLAT);
		final FormData formData = new FormData();
		formData.bottom = new FormAttachment(0, 25);
		formData.top = new FormAttachment(0, 2);
		formData.right = new FormAttachment(100, -80);
		formData.left = new FormAttachment(100, -140);
		btnLogin.setLayoutData(formData);
		btnLogin.setText("Login");
		
		return btnLogin;
	}
	
	private Button createCancelButton(Composite cmpButtonBar){
		final Button btnCancel = new Button(cmpButtonBar, SWT.FLAT);
		final FormData formData = new FormData();
		formData.bottom = new FormAttachment(0, 25);
		formData.top = new FormAttachment(0, 2);
		formData.right = new FormAttachment(100, -3);
		formData.left = new FormAttachment(100, -60);
		btnCancel.setLayoutData(formData);
		btnCancel.setText("Cancel");
		
		return btnCancel;
	}
	
	public boolean checkLogin(){
		enableDialogUI(false);
		ADUser user = null;
		try {
			if (checkUserInput()) {
				user = Login.doLogin(txtUsername.getText(), txtPassword.getText());
				Env.setContext(Env.getCtx(), "#AD_User_Id", user.getObjectId());
				Env.setContext(Env.getCtx(), "#AD_User_Name", user.getUserName());
				
				if (user.getIsInValid()) {
					throw new ClientException("error.user_invalid");
				}
				if (user.getPwdLife() != null && user.getPwdChanged() != null) {
					java.util.GregorianCalendar calender = new java.util.GregorianCalendar();
					calender.setTime(user.getPwdChanged());
					calender.add(java.util.Calendar.DAY_OF_YEAR, user.getPwdLife().intValue());
					ADManager manager = Framework.getService(ADManager.class);
					if (calender.getTime().compareTo(manager.getSysDate()) < 0){
						throw new ClientException("error.password_expiry");
					}
				}
				String language;
				if (comboLanguage.getData() == null 
						|| "".equalsIgnoreCase(comboLanguage.getData().toString())) {
					language = user.getDefLanguage();
				} else {
					language = comboLanguage.getData().toString();
				}
				if (language == null || "".equals(language)) {
					language = "en";
				}
				Locale.setDefault(new Locale(language));
				close();
				return true;
			} else {
				return false;
			}
		} catch (ClientException e) {
			MessageBox mb = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR );
			String msg = Message.getString(e.getErrorCode()) == "" ? e.getErrorCode() : Message.getString(e.getErrorCode());
	    	if (msg == null) {
	    		msg = e.getMessage();
	    	}
			mb.setMessage(msg);
			mb.open();
			if ("error.password_expiry".equals(e.getErrorCode())) {
				if (user != null) {
					PasswordDialog pwdDialog = new PasswordDialog(shell, null);
					pwdDialog.open();
				}
			}
        } catch (Exception e) {
        	logger.error(e);
        } finally {
			enableDialogUI(true);
		}
        return false;
	}
	
	private boolean checkUserInput() {
		// check entries
		String errorMessage = null;
		if ("".equals(txtUsername.getText().trim()))
			errorMessage = "User name can not be null!"; 
		else if ("".equals(txtPassword.getText().trim()))
			errorMessage = "Password can not be null!"; 
		if(errorMessage != null) {
			setMessage(errorMessage);
			setSmartFocus();
			return false;
		}
		return true;
	}
	
	private void setMessage(String message){
		//cblInfo.setForeground(color);
		cblInfo.setText(message);
	}
	private void setSmartFocus() {
		txtPassword.setFocus();
		if("".equals(txtUsername.getText())) {
			txtUsername.setFocus();
		}
	}
	
	private void enableDialogUI(boolean enable) {
		if (shell != null && !shell.isDisposed())
			shell.setEnabled(enable);	
	}
	
	public boolean confirmEntryPage(Display display) throws ClientException {
    	try {
    		ADManager manager = Framework.getService(ADManager.class);
    		ADUser user = new ADUser();
    		user.setObjectId(Env.getUserId());
    		user = (ADUser)manager.getEntity(user);
    		boolean areaConfirm = false;
    		
    		if(user != null) {
    			Object obj = PropertyUtil.getPropertyForIField(user, ORGS);
    	    	List<ADOrg> list = (List<ADOrg>)obj;
    			if(!(list == null || list.size() == 0)) {
    				if(user.getDefaultOrgId() != null) {
    					if(containsDefultOrg(user.getDefaultOrgId(), user)) {
    						Boolean isShowLauncher = (Boolean)PropertyUtil.getPropertyForIField(user, IS_SHOWLAUNCHER);
    						if(!isShowLauncher) {
    							Long orgId = user.getDefaultOrgId();
    							Env.setContext(Env.getCtx(), "#AD_Org_Id", orgId);
    							areaConfirm = true;
    						} else {
    							areaConfirm = entryLauncherDialog(display, user);
    						}
    					} else {				// user's orgs doesn't contain defaultOrgId
    						areaConfirm = entryLauncherDialog(display, user);
    					}
    				} else 	{					// user's defaultOrgId is null
    					areaConfirm = entryLauncherDialog(display, user);
    				}
    				if (areaConfirm) {
    					ADOrg org = new ADOrg();
    					org.setObjectId(Env.getOrgId());
    					org = (ADOrg)manager.getEntity(org);
    					Env.setContext(Env.getCtx(), "#AD_Org_Name", org.getName());
    					
    					user.setLastLogon(new Date());
    					manager.saveEntity(user, Env.getUserId());
    				}
    				return areaConfirm;
    			} else {
    				throw new ClientException("common.noarea_authorization");
    			}
    		}
    	} catch(Exception e) {
    		if (e instanceof ClientException) {
    			ClientException ce = (ClientException)e;
    			MessageBox mb = new MessageBox(new Shell(Display.getCurrent(), SWT.NO_TRIM | SWT.ON_TOP), SWT.OK | SWT.ICON_ERROR );
    			String msg = Message.getString(ce.getErrorCode()) == "" ? ce.getErrorCode() : Message.getString(ce.getErrorCode());
    	    	if (msg == null) {
    	    		msg = e.getMessage();
    	    	}
    			mb.setMessage(msg);
    			mb.open();
    		} else {
    			logger.error("confirmEntryPage error: ", e);
    		}
    	}
    	return false;
    }
    
    private boolean containsDefultOrg(Long defOrg, ADUser user) {
    	Object obj = PropertyUtil.getPropertyForIField(user, ORGS);
    	List<ADOrg> list = (List<ADOrg>)obj;
		for(ADOrg org : list) {
			if(defOrg.equals(org.getObjectId())) {
				return true;
			}
		}
		return false;
    }
	
	protected Object entryMainPage(Display display) {
//		int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
//		if (returnCode == PlatformUI.RETURN_RESTART)
//			return IApplication.EXIT_RESTART;
//		else
//			return IApplication.EXIT_OK;
		return null;
	}
    
    protected boolean entryLauncherDialog(Display display, ADUser user) {
    	LauncherDialog lhDialog = new LauncherDialog(display.getActiveShell(), user);
    	logger.info("Open Dialog to select the work area!");
    	if(lhDialog.open() == Dialog.OK) {
    		// entry Launcher Page
    		return true;
    	}
    	return false;
    }
    
    public boolean close() {
    	if (shell == null || shell.isDisposed()) {
			return true;
		}
    	shell.dispose();
		shell = null;
		
		if (region == null || region.isDisposed()) {
			return true;
		}
		region.dispose();
		region = null;
		
		return true;
    }
	public void setReturnCode(int returnCode) {
		this.returnCode = returnCode;
	}

	public int getReturnCode() {
		return returnCode;
	}
}
