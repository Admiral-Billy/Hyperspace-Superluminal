package com.kartoflane.superluminal2.ui.sidebar;

import java.util.ArrayList;
import java.util.HashMap;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import com.kartoflane.superluminal2.Superluminal;
import com.kartoflane.superluminal2.components.enums.BoardingStrategies;
import com.kartoflane.superluminal2.components.enums.OS;
import com.kartoflane.superluminal2.components.enums.Races;
import com.kartoflane.superluminal2.components.enums.Systems;
import com.kartoflane.superluminal2.core.Cache;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.db.Database;
import com.kartoflane.superluminal2.ftl.AugmentObject;
import com.kartoflane.superluminal2.ftl.DroneList;
import com.kartoflane.superluminal2.ftl.DroneObject;
import com.kartoflane.superluminal2.ftl.ShipObject;
import com.kartoflane.superluminal2.ftl.SystemObject;
import com.kartoflane.superluminal2.ftl.WeaponList;
import com.kartoflane.superluminal2.ftl.WeaponObject;
import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.mvc.controllers.ShipController;
import com.kartoflane.superluminal2.mvc.controllers.SystemController;
import com.kartoflane.superluminal2.ui.AugmentSelectionDialog;
import com.kartoflane.superluminal2.ui.CrewMenu;
import com.kartoflane.superluminal2.ui.DroneSelectionDialog;
import com.kartoflane.superluminal2.ui.EditorWindow;
import com.kartoflane.superluminal2.ui.ShipContainer;
import com.kartoflane.superluminal2.ui.WeaponSelectionDialog;
import com.kartoflane.superluminal2.ui.sidebar.data.DataComposite;
import com.kartoflane.superluminal2.utils.UIUtils;
import com.kartoflane.superluminal2.utils.Utils;


public class PropertiesToolComposite extends Composite implements DataComposite
{
	private static int selectedTab = 0;
	private ShipContainer container;

	private Text txtName;
	private Text txtClass;
	private Text txtDesc;
	private Spinner spHealth;
	private Spinner spPower;
	private TabItem tbtmCrew;
	private Group grpCrew;
	private Composite compCrew;
	private Spinner spCrew;
	private Label lblDesc;
	private Spinner spMinSec;
	private Spinner spMaxSec;
	private TabFolder tabFolder;
	private ArrayList<Button> btnWeapons = new ArrayList<Button>();
	private ArrayList<Button> btnDrones = new ArrayList<Button>();
	private ArrayList<Button> btnAugments = new ArrayList<Button>();
	private ArrayList<Button> btnArtilleries = new ArrayList<Button>();
	private ArrayList<Button> btnCrewMembers = new ArrayList<Button>();
	private HashMap<String, Spinner> spCrewMin = new HashMap<String, Spinner>();
	private HashMap<String, Spinner> spCrewMax = new HashMap<String, Spinner>();
	private Spinner spMissiles;
	private Spinner spWeaponSlots;
	private Button btnWeaponByList;
	private ArrayList<Button> hiddenAugment;
	private Button btnDroneByList;
	private Button btnWeaponList;
	private Button btnDroneList;
	private Group grpWeapons;
	private Composite compArm;
	private Group grpDrones;
	private Spinner spDrones;
	private Spinner spDroneSlots;
	private Label lblBlueprint;
	private Text txtBlueprint;
	private Text txtLayout;
	private Text txtImage;
	private Group grpAugments;
	private CrewMenu crewMenu;
	private Label lblHullHelp;
	private Label lblReactorInfo;
	private Label lblLayoutInfo;
	private Label lblImageHelp;
	private Label lblBlueprintHelp;
	private Combo cmbBoardingAI;
	private Group grpArtillery;
	private Spinner spArtillerySlots;
	private List cmbSlot;


	public PropertiesToolComposite( Composite parent )
	{
		super( parent, SWT.NONE );
		setLayout( new GridLayout( 1, false ) );

		container = Manager.getCurrentShip();
		Image helpImage = Cache.checkOutImage( this, "cpath:/assets/help.png" );

		final ShipObject ship = container.getShipController().getGameObject();
		final boolean[] created = { false };

		Label lblPropertiesTool = new Label( this, SWT.NONE );
		lblPropertiesTool.setLayoutData( new GridData( SWT.CENTER, SWT.TOP, true, false, 1, 1 ) );
		lblPropertiesTool.setText( "Ship Loadout and Properties" );

		Label separator = new Label( this, SWT.SEPARATOR | SWT.HORIZONTAL );
		separator.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false, 1, 1 ) );

		tabFolder = new TabFolder( this, SWT.NONE );
		tabFolder.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true, 1, 1 ) );
		tabFolder.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					if ( created[0] )
						selectedTab = tabFolder.getSelectionIndex();
				}
			}
		);

		/*
		 * =========================================================================
		 * XXX: General tab
		 * =========================================================================
		 */

		TabItem tbtmGeneral = new TabItem( tabFolder, SWT.NONE );
		tbtmGeneral.setText( "General" );

		Composite compGeneral = new Composite( tabFolder, SWT.NONE );
		tbtmGeneral.setControl( compGeneral );
		compGeneral.setLayout( new GridLayout( 4, false ) );

		if ( ship.isPlayerShip() ) {
			
			cmbSlot = new List(compGeneral, SWT.SINGLE);
			cmbSlot.add("A");
			cmbSlot.add("B");
			cmbSlot.add("C");
			if (ship.getBlueprintName().endsWith("_2") || ship.getBlueprintName().endsWith("_B"))
			{
				ship.setLayoutSlot("B");
			}
			else if (ship.getBlueprintName().endsWith("_3") || ship.getBlueprintName().endsWith("_C"))
			{
				ship.setLayoutSlot("C");
			}
			if (ship.getLayoutSlot().equals("A"))
			{
				cmbSlot.setSelection(0);
			}
			else if (ship.getLayoutSlot().equals("B"))
			{
				cmbSlot.setSelection(1);
			}
			else
			{
				cmbSlot.setSelection(2);
			}
			cmbSlot.addSelectionListener(
					new SelectionAdapter() {
						@Override
						public void widgetSelected( SelectionEvent e )
						{
							if (cmbSlot.getSelectionIndex() == 0)
							{
								ship.setLayoutSlot("A");
							}
							else if (cmbSlot.getSelectionIndex() == 1)
							{
								ship.setLayoutSlot("B");
							}
							else
							{
								ship.setLayoutSlot("C");
							}
						}
					}
				);
			
			
			Label lblName = new Label( compGeneral, SWT.NONE );
			lblName.setLayoutData( new GridData( SWT.LEFT, SWT.CENTER, false, false, 4, 1 ) );
			lblName.setText( "Name:" );

			txtName = new Text( compGeneral, SWT.BORDER );
			txtName.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 4, 1 ) );

			txtName.addModifyListener(
				new ModifyListener() {
					@Override
					public void modifyText( ModifyEvent e )
					{
						ship.setShipName( txtName.getText() );
					}
				}
			);
		}
			lblBlueprint = new Label( compGeneral, SWT.NONE );
			lblBlueprint.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 3, 1 ) );
			lblBlueprint.setText( "Slot:" );
			if (ship.isPlayerShip())
			{
				lblBlueprint.setText( "Blueprint Name (start with PLAYER_SHIP_):" );
			}
			else
			{
				lblBlueprint.setText( "Blueprint Name:" );
			}
			lblBlueprintHelp = new Label( compGeneral, SWT.NONE );
			lblBlueprintHelp.setImage( helpImage );
			String msg = "This determines your ship's blueprint name. " +
				"Blueprint name is a unique identifier of the " +
				"ship, that the game uses internally.";
			UIUtils.addTooltip( lblBlueprintHelp, Utils.wrapOSNot( msg, Superluminal.WRAP_WIDTH, Superluminal.WRAP_TOLERANCE, OS.MACOSX() ) );

			txtBlueprint = new Text( compGeneral, SWT.BORDER );
			txtBlueprint.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 4, 1 ) );
			txtBlueprint.setText(ship.getBlueprintName());

			txtBlueprint.addModifyListener(
				new ModifyListener() {
					@Override
					public void modifyText( ModifyEvent e )
					{
						ship.setBlueprintName( txtBlueprint.getText() );
						ship.update();
					}
				}
			);

		Label lblClass = new Label( compGeneral, SWT.NONE );
		lblClass.setLayoutData( new GridData( SWT.LEFT, SWT.CENTER, false, false, 4, 1 ) );
		lblClass.setText( "Class:" );

		txtClass = new Text( compGeneral, SWT.BORDER );
		txtClass.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 4, 1 ) );

		txtClass.addModifyListener(
			new ModifyListener() {
				@Override
				public void modifyText( ModifyEvent e )
				{
					ship.setShipClass( txtClass.getText() );
				}
			}
		);

		if ( ship.isPlayerShip() ) {
			lblDesc = new Label( compGeneral, SWT.NONE );
			lblDesc.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 4, 1 ) );
			lblDesc.setText( "Description: (0/255)" );

			txtDesc = new Text( compGeneral, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI );
			GridData gd_txtDesc = new GridData( SWT.FILL, SWT.CENTER, true, false, 4, 1 );
			gd_txtDesc.heightHint = 80;
			txtDesc.setLayoutData( gd_txtDesc );

			txtDesc.addModifyListener(
				new ModifyListener() {
					@Override
					public void modifyText( ModifyEvent e )
					{
						lblDesc.setText( "Description: (" + txtDesc.getText().length() + "/255)" );
						ship.setShipDescription( txtDesc.getText() );
					}
				}
			);
		}

		Label lblLayout = new Label( compGeneral, SWT.NONE );
		lblLayout.setText( "Layout Filename:" );

		lblLayoutInfo = new Label( compGeneral, SWT.NONE );
		lblLayoutInfo.setLayoutData( new GridData( SWT.RIGHT, SWT.CENTER, false, false, 3, 1 ) );
		lblLayoutInfo.setImage( helpImage );
		String mesg = "This field specifies the name of the ship's layout files, " +
			"like kestral.txt and kestral.xml";
		UIUtils.addTooltip( lblLayoutInfo, Utils.wrapOSNot( mesg, Superluminal.WRAP_WIDTH, Superluminal.WRAP_TOLERANCE, OS.MACOSX() ) );

		txtLayout = new Text( compGeneral, SWT.BORDER );
		txtLayout.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 4, 1 ) );

		txtLayout.addModifyListener(
			new ModifyListener() {
				@Override
				public void modifyText( ModifyEvent e )
				{
					ship.setLayout( txtLayout.getText() );
				}
			}
		);

		Label lblImageNamespace = new Label( compGeneral, SWT.NONE );
		lblImageNamespace.setText( "Image Namespace:" );

		lblImageHelp = new Label( compGeneral, SWT.NONE );
		lblImageHelp.setLayoutData( new GridData( SWT.RIGHT, SWT.CENTER, false, false, 3, 1 ) );
		lblImageHelp.setImage( helpImage );
		msg = "This field specifies the name of the ship's image files, " +
			"like kestral_base.png";
		UIUtils.addTooltip( lblImageHelp, Utils.wrapOSNot( msg, Superluminal.WRAP_WIDTH, Superluminal.WRAP_TOLERANCE, OS.MACOSX() ) );

		txtImage = new Text( compGeneral, SWT.BORDER );
		txtImage.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 4, 1 ) );

		txtImage.addModifyListener(
			new ModifyListener() {
				@Override
				public void modifyText( ModifyEvent e )
				{
					ship.setImageNamespace( txtImage.getText() );
				}
			}
		);

		Label lblHealth = new Label( compGeneral, SWT.NONE );
		lblHealth.setText( "Hull Health:" );

		spHealth = new Spinner( compGeneral, SWT.BORDER );
		spHealth.setTextLimit( 3 );
		spHealth.setMinimum( 0 );
		GridData gd_spHealth = new GridData( SWT.RIGHT, SWT.CENTER, true, false, 2, 1 );
		gd_spHealth.widthHint = 25;
		spHealth.setLayoutData( gd_spHealth );

		spHealth.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					ship.setHealth( spHealth.getSelection() );
				}
			}
		);

		lblHullHelp = new Label( compGeneral, SWT.NONE );
		lblHullHelp.setLayoutData( new GridData( SWT.RIGHT, SWT.CENTER, false, false, 1, 1 ) );
		lblHullHelp.setImage( helpImage );
		msg = "This defines the amount of health your ship starts with. " +
			"The default is 30 for a player ship.";
		UIUtils.addTooltip( lblHullHelp, Utils.wrapOSNot( msg, Superluminal.WRAP_WIDTH, Superluminal.WRAP_TOLERANCE, OS.MACOSX() ) );

		Label lblReactor = new Label( compGeneral, SWT.NONE );
		lblReactor.setText( "Reactor Power:" );

		spPower = new Spinner( compGeneral, SWT.BORDER );
		spPower.setTextLimit( 3 );
		spPower.setMinimum( 0 );
		GridData gd_spPower = new GridData( SWT.RIGHT, SWT.CENTER, true, false, 2, 1 );
		gd_spPower.widthHint = 25;
		spPower.setLayoutData( gd_spPower );

		spPower.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					ship.setPower( spPower.getSelection() );
				}
			}
		);

		lblReactorInfo = new Label( compGeneral, SWT.NONE );
		lblReactorInfo.setLayoutData( new GridData( SWT.RIGHT, SWT.CENTER, false, false, 1, 1 ) );
		lblReactorInfo.setImage( helpImage );
		msg = "This sets the amount of power the ship starts with. " +
			"The reactor can only be upgraded to level 25 ingame, " +
			"but can be set to a higher value when creating your " +
			"ship in this editor.";
		UIUtils.addTooltip( lblReactorInfo, Utils.wrapOSNot( msg, Superluminal.WRAP_WIDTH, Superluminal.WRAP_TOLERANCE, OS.MACOSX() ) );

		if ( !ship.isPlayerShip() ) {
			Label lblMinSector = new Label( compGeneral, SWT.NONE );
			lblMinSector.setText( "Min Sector:" );

			spMinSec = new Spinner( compGeneral, SWT.BORDER );
			spMinSec.setTextLimit( 1 );
			spMinSec.setMaximum( 8 );
			spMinSec.setMinimum( 1 );
			GridData gd_spMinSec = new GridData( SWT.RIGHT, SWT.CENTER, false, false, 2, 1 );
			gd_spMinSec.widthHint = 25;
			spMinSec.setLayoutData( gd_spMinSec );

			spMinSec.addSelectionListener(
				new SelectionAdapter() {
					@Override
					public void widgetSelected( SelectionEvent e )
					{
						ship.setMinSector( spMinSec.getSelection() );
					}
				}
			);

			Label lblMinSecInfo = new Label( compGeneral, SWT.NONE );
			lblMinSecInfo.setImage( helpImage );
			msg = "This determines the minimum sector in which the enemy can be encountered.";
			UIUtils.addTooltip( lblMinSecInfo, Utils.wrapOSNot( msg, Superluminal.WRAP_WIDTH, Superluminal.WRAP_TOLERANCE, OS.MACOSX() ) );

			Label lblMaxSector = new Label( compGeneral, SWT.NONE );
			lblMaxSector.setText( "Max Sector:" );

			spMaxSec = new Spinner( compGeneral, SWT.BORDER );
			spMaxSec.setTextLimit( 1 );
			spMaxSec.setMaximum( 8 );
			spMaxSec.setMinimum( 1 );
			GridData gd_spMaxSec = new GridData( SWT.RIGHT, SWT.CENTER, false, false, 2, 1 );
			gd_spMaxSec.widthHint = 25;
			spMaxSec.setLayoutData( gd_spMaxSec );

			spMaxSec.addSelectionListener(
				new SelectionAdapter() {
					@Override
					public void widgetSelected( SelectionEvent e )
					{
						ship.setMaxSector( spMaxSec.getSelection() );
					}
				}
			);

			Label lblMaxSecInfo = new Label( compGeneral, SWT.NONE );
			lblMaxSecInfo.setImage( helpImage );
			msg = "This determines the maximum sector in which the enemy can be encountered.";
			UIUtils.addTooltip( lblMaxSecInfo, Utils.wrapOSNot( msg, Superluminal.WRAP_WIDTH, Superluminal.WRAP_TOLERANCE, OS.MACOSX() ) );

			Label lblBoardingAI = new Label( compGeneral, SWT.NONE );
			lblBoardingAI.setText( "Boarding AI:" );

			cmbBoardingAI = new Combo( compGeneral, SWT.READ_ONLY );
			cmbBoardingAI.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 2, 1 ) );
			for ( BoardingStrategies ai : BoardingStrategies.values() ) {
				cmbBoardingAI.add( ai.toString() );
			}

			cmbBoardingAI.addSelectionListener(
				new SelectionAdapter() {
					@Override
					public void widgetSelected( SelectionEvent e )
					{
						ship.setBoardingAI( BoardingStrategies.values()[cmbBoardingAI.getSelectionIndex()] );
					}
				}
			);

			Label lblBoardingAIInfo = new Label( compGeneral, SWT.NONE );
			lblBoardingAIInfo.setLayoutData( new GridData( SWT.RIGHT, SWT.CENTER, false, false, 1, 1 ) );
			lblBoardingAIInfo.setImage( helpImage );
			msg = "This determines the strategy that the ship's crew is going to use when boarding.";
			UIUtils.addTooltip( lblBoardingAIInfo, Utils.wrapOSNot( msg, Superluminal.WRAP_WIDTH, Superluminal.WRAP_TOLERANCE, OS.MACOSX() ) );
		}

		/*
		 * =========================================================================
		 * XXX: Armaments tab
		 * =========================================================================
		 */

		TabItem tbtmArmaments = new TabItem( tabFolder, 0 );
		tbtmArmaments.setText( "Armaments" );

		compArm = new Composite( tabFolder, SWT.NONE );
		tbtmArmaments.setControl( compArm );
		compArm.setLayout( new GridLayout( 1, false ) );

		/*
		 * =========================================================================
		 * XXX: Weapons
		 * =========================================================================
		 */

		grpWeapons = new Group( compArm, SWT.NONE );
		grpWeapons.setLayout( new GridLayout( 2, false ) );
		grpWeapons.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false, 1, 1 ) );
		grpWeapons.setText( "Weapons" );

		Label lblMissiles = new Label( grpWeapons, SWT.NONE );
		lblMissiles.setText( "Starting Missiles" );

		spMissiles = new Spinner( grpWeapons, SWT.BORDER );
		spMissiles.setMaximum( 999 );
		GridData gd_spMissiles = new GridData( SWT.RIGHT, SWT.CENTER, true, false, 1, 1 );
		gd_spMissiles.widthHint = 25;
		spMissiles.setLayoutData( gd_spMissiles );

		spMissiles.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					ship.setMissilesAmount( spMissiles.getSelection() );
				}
			}
		);

		if ( !ship.isPlayerShip() ) {
			btnWeaponByList = new Button( grpWeapons, SWT.CHECK );
			btnWeaponByList.setLayoutData( new GridData( SWT.FILL, SWT.LEFT, true, false, 1, 1 ) );
			btnWeaponByList.setText( "Weapons By List" );
			btnWeaponByList.setSelection( ship.getWeaponsByList() );

			Label lblWeaponByListInfo = new Label( grpWeapons, SWT.NONE );
			lblWeaponByListInfo.setImage( helpImage );
			lblWeaponByListInfo.setLayoutData( new GridData( SWT.RIGHT, SWT.CENTER, true, false, 1, 1 ) );
			msg = "Unchecking this allows you to define this ship's weapons directly, like in player ships.";
			UIUtils.addTooltip( lblWeaponByListInfo, Utils.wrapOSNot( msg, Superluminal.WRAP_WIDTH, Superluminal.WRAP_TOLERANCE, OS.MACOSX() ) );

			btnWeaponByList.addSelectionListener(
				new SelectionAdapter() {
					@Override
					public void widgetSelected( SelectionEvent e )
					{
						btnWeaponList.dispose();
						clearWeaponSlots();
						ship.setWeaponsByList( btnWeaponByList.getSelection() );
						if ( ship.getWeaponsByList() ) {
							//TODO: for (int i = 0; i < ship.getWeaponSlots(); ++i)
							createWeaponList( ship );
						}
						else {
							createWeaponSlots( ship.getWeaponSlots() );
						}
						updateData();
						container.updateMounts();
						EditorWindow.getInstance().updateSidebarScroll();
					}
				}
			);
		}

		Label lblWeaponSlots = new Label( grpWeapons, SWT.NONE );
		lblWeaponSlots.setText( "Slots" );

		spWeaponSlots = new Spinner( grpWeapons, SWT.BORDER );
		spWeaponSlots.setMaximum( 8 );
		GridData gd_spWeaponSlots = new GridData( SWT.RIGHT, SWT.CENTER, true, false, 1, 1 );
		gd_spWeaponSlots.widthHint = 25;
		spWeaponSlots.setLayoutData( gd_spWeaponSlots );
		spWeaponSlots.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					if ( ship.getWeaponsByList() ) {
						ship.setWeaponSlots( spWeaponSlots.getSelection() );
						updateData();
					}
					else {
						if ( spWeaponSlots.getSelection() > 4 && ship.getWeaponSlots() <= 4 && !Manager.shownSlotWarning ) {
							String msg = "Giving a ship more than 4 weapon slots will cause ingame UI to break.\n" +
								"On top of that, toggling autofire on weapons assigned to slots >4 will crash the game.\n" +
								"While it's possible for a ship to have any number of weapon slots, the\n" +
								"option to have more than 4 should only be used by experienced modders.";
							UIUtils.showWarningDialog( EditorWindow.getInstance().getShell(), null, msg );
							Manager.shownSlotWarning = true;
						}

						int slots = spWeaponSlots.getSelection();
						ship.setWeaponSlots( slots );
						clearWeaponSlots();
						createWeaponSlots( slots );
						updateData();
						container.updateMounts();

						EditorWindow.getInstance().updateSidebarScroll();
					}
				}
			}
		);

		if ( ship.getWeaponsByList() ) {
			//TODO: for (int i = 0; i < ship.getWeaponSlots(); ++i)
			createWeaponList( ship );
		}
		else {
			createWeaponSlots( ship.getWeaponSlots() );
		}

		/*
		 * =========================================================================
		 * XXX: Drones
		 * =========================================================================
		 */

		grpDrones = new Group( compArm, SWT.NONE );
		grpDrones.setLayout( new GridLayout( 2, false ) );
		grpDrones.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false, 1, 1 ) );
		grpDrones.setText( "Drones" );

		Label lblDrones = new Label( grpDrones, SWT.NONE );
		lblDrones.setText( "Starting Drone Parts" );

		spDrones = new Spinner( grpDrones, SWT.BORDER );
		spDrones.setMaximum( 999 );
		GridData gd_spDrones = new GridData( SWT.RIGHT, SWT.CENTER, true, false, 1, 1 );
		gd_spDrones.widthHint = 25;
		spDrones.setLayoutData( gd_spDrones );

		spDrones.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					ship.setDronePartsAmount( spDrones.getSelection() );
				}
			}
		);

		if ( !ship.isPlayerShip() ) {
			btnDroneByList = new Button( grpDrones, SWT.CHECK );
			btnDroneByList.setLayoutData( new GridData( SWT.FILL, SWT.LEFT, true, false, 1, 1 ) );
			btnDroneByList.setText( "Drones By List" );
			btnDroneByList.setSelection( ship.getDronesByList() );

			Label lblDroneByListInfo = new Label( grpDrones, SWT.NONE );
			lblDroneByListInfo.setImage( helpImage );
			lblDroneByListInfo.setLayoutData( new GridData( SWT.RIGHT, SWT.CENTER, true, false, 1, 1 ) );
			msg = "Unchecking this allows you to define this ship's drones directly, like in player ships.";
			UIUtils.addTooltip( lblDroneByListInfo, Utils.wrapOSNot( msg, Superluminal.WRAP_WIDTH, Superluminal.WRAP_TOLERANCE, OS.MACOSX() ) );

			btnDroneByList.addSelectionListener(
				new SelectionAdapter() {
					@Override
					public void widgetSelected( SelectionEvent e )
					{
						btnDroneList.dispose();
						clearDroneSlots();
						ship.setDronesByList( btnDroneByList.getSelection() );
						if ( ship.getDronesByList() ) {
							createDroneList( ship );
						}
						else {
							createDroneSlots( ship.getDroneSlots() );
						}
						updateData();
						EditorWindow.getInstance().updateSidebarScroll();
					}
				}
			);
		}

		Label lblDroneSlots = new Label( grpDrones, SWT.NONE );
		lblDroneSlots.setText( "Slots" );

		spDroneSlots = new Spinner( grpDrones, SWT.BORDER );
		spDroneSlots.setMaximum( 8 );
		GridData gd_spDroneSlots = new GridData( SWT.RIGHT, SWT.CENTER, true, false, 1, 1 );
		gd_spDroneSlots.widthHint = 25;
		spDroneSlots.setLayoutData( gd_spDroneSlots );
		spDroneSlots.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					if ( ship.getDronesByList() ) {
						ship.setDroneSlots( spDroneSlots.getSelection() );
						updateData();
					}
					else {
						if ( spDroneSlots.getSelection() > 4 && ship.getDroneSlots() <= 4 && !Manager.shownSlotWarning ) {
							String msg = "Giving a ship more than 4 drone slots will cause ingame UI to break.\n" +
								"While it's possible for a ship to have any number of drone slots, this\n" +
								"option should only be used by experienced modders.";
							UIUtils.showWarningDialog( EditorWindow.getInstance().getShell(), null, msg );
							Manager.shownSlotWarning = true;
						}

						int slots = spDroneSlots.getSelection();
						ship.setDroneSlots( slots );
						clearDroneSlots();
						createDroneSlots( slots );
						updateData();

						EditorWindow.getInstance().updateSidebarScroll();
					}
				}
			}
		);

		if ( ship.getDronesByList() ) {
			createDroneList( ship );
		}
		else {
			createDroneSlots( ship.getDroneSlots() );
		}

		/*
		 * =========================================================================
		 * XXX: Augments
		 * =========================================================================
		 */

		grpAugments = new Group( compArm, SWT.NONE );
		grpAugments.setLayout( new GridLayout( 1, false ) );
		grpAugments.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false, 1, 1 ) );
		grpAugments.setText( "Augments" );
		hiddenAugment = new ArrayList<Button>();
		SelectionAdapter augmentListener = new SelectionAdapter() {
			@Override
			public void widgetSelected( SelectionEvent e )
			{
				int i = btnAugments.indexOf( e.getSource() );

				if ( i != -1 ) {
					ShipObject ship = container.getShipController().getGameObject();
					AugmentObject current = ship.getAugments()[i];

					AugmentSelectionDialog dialog = new AugmentSelectionDialog( EditorWindow.getInstance().getShell() );
					AugmentObject neu = dialog.open( current );

					if ( neu != null ) {
						// If the augment is the default dummy, then replace the first occurence of
						// the dummy aug, so that there are no gaps
						if ( current == Database.DEFAULT_AUGMENT_OBJ )
							ship.changeAugment( current, neu );
						else
							ship.changeAugment( i, neu );
						updateData();
					}
				}
			}
		};
		SelectionListener hiddenListener = new SelectionListener() {
			@Override
			public void widgetSelected( SelectionEvent e )
			{
				
				if (ship.getAugments()[hiddenAugment.indexOf(e.getSource())] != Database.DEFAULT_AUGMENT_OBJ)
				ship.getAugments()[hiddenAugment.indexOf(e.getSource())].isHidden = hiddenAugment.get(hiddenAugment.indexOf(e.getSource())).getSelection();
				updateData();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				updateData();
			}
		};
		Button augBtn;
		if (ship.isPlayerShip())
		{
			for ( int i = 0; i < 10; i++ ) {
				augBtn = new Button( grpAugments, SWT.NONE );
				augBtn.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );
				augBtn.setText( "<hidden augment slot>" );
				augBtn.addSelectionListener( augmentListener );
				btnAugments.add(augBtn);
				Button hdnAug = new Button( grpAugments, SWT.CHECK);
				hdnAug.setLayoutData( new GridData( SWT.FILL, SWT.LEFT, true, false, 1, 1 ) );
				hdnAug.setText( "Hidden augment?" );
				hdnAug.addSelectionListener(hiddenListener);
				hiddenAugment.add(hdnAug);
			}
		}
		else
		{
			for ( int i = 0; i < 3; i++ ) {
				augBtn = new Button( grpAugments, SWT.NONE );
				augBtn.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );
				augBtn.setText( "<hidden augment slot>" );
				augBtn.addSelectionListener( augmentListener );
				btnAugments.add(augBtn);
			}
		}

		/*
		 * =========================================================================
		 * XXX: Artillery
		 * =========================================================================
		 */

		grpArtillery = new Group( compArm, SWT.NONE );
		grpArtillery.setLayout( new GridLayout( 2, false ) );
		grpArtillery.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false, 1, 1 ) );
		grpArtillery.setText( "Artillery" );

		Label lblArtillerySlots = new Label( grpArtillery, SWT.NONE );
		lblArtillerySlots.setText( "Slots:" );

		spArtillerySlots = new Spinner( grpArtillery, SWT.BORDER );
		spArtillerySlots.setMaximum( 8 );
		spArtillerySlots.setMinimum( 1 );
		GridData gd_spArtillerySlots = new GridData( SWT.RIGHT, SWT.CENTER, true, false, 1, 1 );
		gd_spArtillerySlots.widthHint = 25;
		spArtillerySlots.setLayoutData( gd_spArtillerySlots );
		spArtillerySlots.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					ArrayList<SystemObject> systems = ship.getSystems( Systems.ARTILLERY );
					int slots = spArtillerySlots.getSelection();

					if ( slots > 1 && systems.size() <= 1 && !Manager.shownArtilleryWarning ) {
						String msg = "The player can only control power of the first artillery system.\n" +
							"All the other artillery systems will be permanently powered, and cannot be upgraded.";
						UIUtils.showWarningDialog( EditorWindow.getInstance().getShell(), null, msg );
						Manager.shownArtilleryWarning = true;
					}

					if ( systems.size() < slots ) {
						for ( int i = systems.size(); i < slots; i++ ) {
							SystemObject system = new SystemObject( Systems.ARTILLERY, ship );
							system.setAlias( "#" + ( i + 1 ) );
							SystemController sysC = SystemController.newInstance( container, system );
							ship.add( system );
							container.add( sysC );
							container.store( sysC );
						}
					}
					else {
						for ( int i = systems.size() - 1; i >= slots; i-- ) {
							SystemObject system = systems.get( i );
							SystemController sysC = (SystemController)container.getController( system );
							container.unassign( system );
							container.remove( sysC );
							container.dispose( sysC );
							ship.remove( system );
						}
					}

					clearArtillerySlots();
					createArtillerySlots( slots );
					updateData();
					container.updateMounts();

					EditorWindow.getInstance().updateSidebarScroll();
				}
			}
		);
		createArtillerySlots( ship.getSystems( Systems.ARTILLERY ).size() );

		/*
		 * =========================================================================
		 * XXX: Crew tab
		 * =========================================================================
		 */

		tbtmCrew = new TabItem( tabFolder, SWT.NONE );
		tbtmCrew.setText( "Crew" );

		compCrew = new Composite( tabFolder, SWT.NONE );
		tbtmCrew.setControl( compCrew );
		compCrew.setLayout( new GridLayout( 3, false ) );

		if ( ship.isPlayerShip() ) {
			grpCrew = new Group( compCrew, SWT.NONE );
			grpCrew.setLayout( new GridLayout( 2, false ) );
			grpCrew.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false, 1, 1 ) );
			grpCrew.setText( "Crew" );
			grpCrew.setVisible(true);
			Label lblCrew = new Label( grpCrew, SWT.NONE );
			lblCrew.setText( "Max (Hit Enter after change)" );
			lblCrew.setVisible(true);
			
			spCrew = new Spinner( grpCrew, SWT.BORDER );
			spCrew.setMaximum( 999 );
			spCrew.setSelection(ship.getCrewCap());
			GridData gd_spCrew = new GridData( SWT.RIGHT, SWT.CENTER, true, false, 1, 1 );
			gd_spCrew.widthHint = 25;
			spCrew.setLayoutData( gd_spCrew );
			spCrew.setVisible(true);
			SelectionAdapter listener = new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					int i = btnCrewMembers.indexOf( e.getSource() );

					if ( i != -1 ) {
						ShipObject ship = container.getShipController().getGameObject();
						String current = ship.getCrew()[i];
						for (int k = 0; k < Races.getPlayerRaces().length; ++k)
						{
							if (current.equals(Races.getPlayerRaces()[k]))
							{
								current = Races.getPlayerRacesAliases()[k];
							}
						}

						crewMenu = new CrewMenu( compCrew );
						String neu = crewMenu.open();

						if ( neu != null ) {
							if ( current.equals("no_crew") )
								ship.changeCrew( current, neu );
							else
								ship.changeCrew( i, neu );
							updateData();
						}
					}
				}};
				
				btnCrewMembers.clear();
				for ( int i = 0; i < ship.getCrewCap(); i++ ) {
					Button btn = new Button( compCrew, SWT.NONE );
					btn.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 3, 1 ) );
					btn.setText( "<crew slot>" );
					btn.addSelectionListener( listener );
					btnCrewMembers.add( btn );
				}
				
//			spCrew.addSelectionListener(
//				new SelectionAdapter() {
//					@Override
//					public void widgetSelected( SelectionEvent e )
//					{
//						ship.setCrewCap( spCrew.getSelection() );
//						for ( Button b : btnCrewMembers )
//							b.dispose();
//						btnCrewMembers.clear();
//						for ( int i = 0; i < ship.getCrewCap(); i++ ) {
//							Button btn = new Button( compCrew, SWT.NONE );
//							btn.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 3, 1 ) );
//							btn.setText( "<crew slot>" );
//							btn.addSelectionListener( listener );
//							ship.coalesceCrew();
//							btnCrewMembers.add( btn );
//						}
//						compCrew.layout();
//						updateData();
//						EditorWindow.getInstance().updateSidebarScroll();
//					}
//				});
			spCrew.addFocusListener(
					new FocusListener() {

						@Override
						public void focusGained(FocusEvent arg0) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void focusLost(FocusEvent arg0) {
							ship.setCrewCap( spCrew.getSelection() );
							for ( Button b : btnCrewMembers )
								b.dispose();
							btnCrewMembers.clear();
							for ( int i = 0; i < ship.getCrewCap(); i++ ) {
								Button btn = new Button( compCrew, SWT.NONE );
								btn.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 3, 1 ) );
								btn.setText( "<crew slot>" );
								btn.addSelectionListener( listener );
								ship.coalesceCrew();
								btnCrewMembers.add( btn );
							}
							compCrew.layout();
							updateData();
							EditorWindow.getInstance().updateSidebarScroll();
						}
						
					});
			spCrew.addTraverseListener(
					new TraverseListener() {

						@Override
						public void keyTraversed(TraverseEvent arg0) {
							// TODO Auto-generated method stub
							if (arg0.detail == SWT.TRAVERSE_RETURN)
							{
								ship.setCrewCap( spCrew.getSelection() );
								for ( Button b : btnCrewMembers )
									b.dispose();
								btnCrewMembers.clear();
								for ( int i = 0; i < ship.getCrewCap(); i++ ) {
									Button btn = new Button( compCrew, SWT.NONE );
									btn.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 3, 1 ) );
									btn.setText( "<crew slot>" );
									btn.addSelectionListener( listener );
									ship.coalesceCrew();
									btnCrewMembers.add( btn );
								}
								compCrew.layout();
								updateData();
								EditorWindow.getInstance().updateSidebarScroll();
							}
						} });
		}
		else {
			spCrewMin.clear();
			spCrewMax.clear();

			Label lbl = new Label( compCrew, SWT.NONE );
			lbl.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );
			lbl.setText( "Random Crew:" );

			lbl = new Label( compCrew, SWT.NONE );
			lbl.setText( "Min" );

			lbl = new Label( compCrew, SWT.NONE );
			lbl.setText( "Max" );
			

			for ( String race : Races.getRacesAliases() ) {
				final String r = race;
				final String ra = Races.getRaces()[Races.raceAliasList.indexOf(race)];
				lbl = new Label( compCrew, SWT.NONE );
				lbl.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );
				lbl.setText( race.toString() );

				final Spinner spMin = new Spinner( compCrew, SWT.BORDER );
				final Spinner spMax = new Spinner( compCrew, SWT.BORDER );
				spMin.setMaximum( 99 );
				spCrewMin.put( Races.getRaces()[Races.raceAliasList.indexOf(race)], spMin );

				spMax.setMaximum( 99 );
				spCrewMax.put( Races.getRaces()[Races.raceAliasList.indexOf(race)], spMax );

				spMin.addSelectionListener(
					new SelectionAdapter() {
						@Override
						public void widgetSelected( SelectionEvent e )
						{
							ship.setCrewMin( ra, spMin.getSelection() );
							if ( ship.getCrewMax( ra ) < ship.getCrewMin( ra ) ) {
								spMax.setSelection( ship.getCrewMin( ra ) );
								spMax.notifyListeners( SWT.Selection, null );
							}
						}
					}
				);

				spMax.addSelectionListener(
					new SelectionAdapter() {
						@Override
						public void widgetSelected( SelectionEvent e )
						{
							ship.setCrewMax( ra, spMax.getSelection() );
						}
					}
				);
			}
		}

		pack();
		updateData();
		created[0] = true;
		tabFolder.setSelection( selectedTab );
	}

	public void updateData()
	{
		ShipController controller = container.getShipController();
		ShipObject ship = controller.getGameObject();

		String content = null;
		// General tab

		content = ship.getShipClass().toString();
		txtClass.setText( content == null ? "Ship Class" : content );

		content = ship.getLayout();
		txtLayout.setText( content == null ? "myship" : content );

		content = ship.getImageNamespace();
		txtImage.setText( content == null ? "myship" : content );

		spHealth.setSelection( ship.getHealth() );
		spPower.setSelection( ship.getPower() );

		if ( ship.isPlayerShip() ) {
			content = ship.getShipName().toString();
			txtName.setText( ship.isPlayerShip() && content == null ? "The Nameless One" : content );

			content = ship.getShipDescription().toString();
			txtDesc.setText( ship.isPlayerShip() && content == null ? "Ship Class" : content );
			lblDesc.setText( "Description: (" + txtDesc.getText().length() + "/255)" );
		}
		else {
			txtBlueprint.setText( ship.getBlueprintName() );

			spMinSec.setSelection( ship.getMinSector() );
			spMaxSec.setSelection( ship.getMaxSector() );
			cmbBoardingAI.select( ship.getBoardingAI().ordinal() );
		}

		// Armaments tab
		if(ship.isPlayerShip())
		spMissiles.setSelection( ship.getMissilesAmount() );
		spWeaponSlots.setSelection( ship.getWeaponSlots() );
		spDrones.setSelection( ship.getDronePartsAmount() );
		spDroneSlots.setSelection( ship.getDroneSlots() );

		int count = 0;
		if ( ship.getWeaponsByList() ) {
			WeaponList wList = ship.getWeaponList();
			btnWeaponList.setText( wList.getBlueprintName() );
		}
		else {
			count = 0;
			for ( WeaponObject weapon : ship.getWeapons() ) {
				if ( count < ship.getWeaponSlots() ) {
					btnWeapons.get( count ).setText( weapon.toString() );
					count++;
				}
			}
		}

		if ( ship.getDronesByList() ) {
			DroneList dList = ship.getDroneList();
			btnDroneList.setText( dList.getBlueprintName() );
		}
		else {
			count = 0;
			for ( DroneObject drone : ship.getDrones() ) {
				if ( count < ship.getDroneSlots() ) {
					btnDrones.get( count ).setText( drone.toString() );
					count++;
				}
			}
		}

		count = 0;
		for ( AugmentObject augment : ship.getAugments() ) {
			if (btnAugments.size() != 0)
			{
			btnAugments.get( count ).setText( augment.toString() );
			if(hiddenAugment.size() != 0)
			hiddenAugment.get(count).setSelection(augment.isHidden);
			}
			count++;
		}

		spArtillerySlots.setSelection( ship.getSystems( Systems.ARTILLERY ).size() );
		count = 0;
		for ( SystemObject system : ship.getSystems( Systems.ARTILLERY ) ) {
			WeaponObject weapon = system.getWeapon();
			btnArtilleries.get( count ).setText( weapon.toString() );
			count++;
		}

		// Crew tab
		if ( ship.isPlayerShip() ) {
			count = 0;
			for ( String race : ship.getCrew() ) 
			{
						if (count < ship.getCrewCap())
						{
							if (Races.raceList.contains(race.toLowerCase()))
							{
								btnCrewMembers.get( count ).setText( Races.getPlayerRacesAliases()[Races.raceList.indexOf(race.toLowerCase())] );
								count++;
							}
							else
							{
								btnCrewMembers.get( count ).setText( race.toString() );
								count++;
							}
						}
			}
		}
		else {
			for ( String race : Races.getRaces() ) {
				try
				{
				spCrewMin.get( race ).setSelection( ship.getCrewMin( race ) );
				spCrewMax.get( race ).setSelection( ship.getCrewMax( race ) );
				}
				catch (NullPointerException e)
				{

				}
			}
		}
	}

	private void clearWeaponSlots()
	{
		for ( Button b : btnWeapons )
			b.dispose();
		btnWeapons.clear();
		compArm.layout();
	}

	private void clearDroneSlots()
	{
		for ( Button b : btnDrones )
			b.dispose();
		btnDrones.clear();
		compArm.layout();
	}

	private void clearArtillerySlots()
	{
		for ( Button b : btnArtilleries )
			b.dispose();
		btnArtilleries.clear();
		compArm.layout();
	}

	private void createWeaponList( final ShipObject ship )
	{
		btnWeaponList = new Button( grpWeapons, SWT.NONE );
		btnWeaponList.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 2, 1 ) );
		btnWeaponList.setText( "<weapon list>" );

		btnWeaponList.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					WeaponList current = ship.getWeaponList();
					WeaponSelectionDialog dialog = new WeaponSelectionDialog( EditorWindow.getInstance().getShell() );
					WeaponList neu = dialog.open( current );

					if ( neu != null ) {
						ship.setWeaponList( neu );
						updateData();
					}
				}
			}
		);

		compArm.layout();
	}

	private void createWeaponSlots( int n )
	{
		SelectionAdapter listener = new SelectionAdapter() {
			@Override
			public void widgetSelected( SelectionEvent e )
			{
				int i = btnWeapons.indexOf( e.getSource() );

				if ( i != -1 ) {
					ShipObject ship = container.getShipController().getGameObject();
					WeaponObject current = ship.getWeapons()[i];

					WeaponSelectionDialog dialog = new WeaponSelectionDialog( EditorWindow.getInstance().getShell() );
					WeaponObject neu = dialog.open( current );

					if ( neu != null ) {
						// If the weapon is the default dummy, then replace the first occurence of
						// the dummy weapon, so that there are no gaps
						if ( current == Database.DEFAULT_WEAPON_OBJ )
							container.changeWeapon( current, neu );
						else
							container.changeWeapon( i, neu );
						updateData();
					}
				}
			}
		};

		for ( int i = 0; i < n; i++ ) {
			Button b = new Button( grpWeapons, SWT.NONE );
			b.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 2, 1 ) );
			b.setText( "<weapon slot>" );
			b.addSelectionListener( listener );
			btnWeapons.add( b );
		}

		compArm.layout();
	}

	private void createDroneList( final ShipObject ship )
	{
		btnDroneList = new Button( grpDrones, SWT.NONE );
		btnDroneList.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 2, 1 ) );
		btnDroneList.setText( "<drone list>" );

		btnDroneList.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					DroneList current = ship.getDroneList();
					DroneSelectionDialog dialog = new DroneSelectionDialog( EditorWindow.getInstance().getShell() );
					DroneList neu = dialog.open( current );

					if ( neu != null ) {
						ship.setDroneList( neu );
						updateData();
					}
				}
			}
		);

		compArm.layout();
	}

	private void createDroneSlots( int n )
	{
		SelectionAdapter listener = new SelectionAdapter() {
			@Override
			public void widgetSelected( SelectionEvent e )
			{
				int i = btnDrones.indexOf( e.getSource() );

				if ( i != -1 ) {
					ShipObject ship = container.getShipController().getGameObject();
					DroneObject current = ship.getDrones()[i];

					DroneSelectionDialog dialog = new DroneSelectionDialog( EditorWindow.getInstance().getShell() );
					DroneObject neu = dialog.open( current );

					if ( neu != null ) {
						// If the drone is the default dummy, then replace the first occurence of
						// the dummy drone, so that there are no gaps
						if ( current == Database.DEFAULT_DRONE_OBJ )
							ship.changeDrone( current, neu );
						else
							ship.changeDrone( i, neu );
						updateData();
					}
				}
			}
		};

		for ( int i = 0; i < n; i++ ) {
			Button b = new Button( grpDrones, SWT.NONE );
			b.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 2, 1 ) );
			b.setText( "<drone slot>" );
			b.addSelectionListener( listener );
			btnDrones.add( b );
		}

		compArm.layout();
	}

	private void createArtillerySlots( int n )
	{
		SelectionAdapter listener = new SelectionAdapter() {
			@Override
			public void widgetSelected( SelectionEvent e )
			{
				int i = btnArtilleries.indexOf( e.getSource() );

				if ( i != -1 ) {
					ShipObject ship = container.getShipController().getGameObject();
					SystemObject system = ship.getSystems( Systems.ARTILLERY ).get( i );
					WeaponObject current = system.getWeapon();

					WeaponSelectionDialog dialog = new WeaponSelectionDialog( EditorWindow.getInstance().getShell() );
					WeaponObject neu = dialog.open( current );

					if ( neu != null ) {
						// If no weapon was selected, default to ARTILLERY_FED
						Database db = Database.getInstance();
						if ( neu == Database.DEFAULT_WEAPON_OBJ && db != null ) {
							neu = db.getWeapon( "ARTILLERY_FED" );
						}

						system.setWeapon( neu );
						updateData();
						container.updateMounts();
					}
				}
			}
		};

		for ( int i = 0; i < n; i++ ) {
			Button btn = new Button( grpArtillery, SWT.NONE );
			btn.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 2, 1 ) );
			btn.setText( "<artillery slot>" );
			btn.addSelectionListener( listener );
			btnArtilleries.add( btn );
		}

		compArm.layout();
	}

	@Override
	public boolean isFocusControl()
	{
		Control c = UIUtils.getDisplay().getFocusControl();
		boolean result = c == txtClass || c == spHealth || c == spPower || c == spMissiles || c == spWeaponSlots ||
			c == spDrones || c == spDroneSlots || c == txtLayout || c == txtImage;
		if ( container.getShipController().isPlayerShip() ) {
			result |= c == txtName || c == txtDesc;
		}
		else {
			result |= c == spMinSec || c == spMaxSec || c == txtBlueprint;
		}
		return result;
	}

	public void setController( AbstractController c )
	{
		throw new UnsupportedOperationException();
	}

	public void reloadController()
	{
	}

	@Override
	public void dispose()
	{
		super.dispose();
		Cache.checkInImage( this, "cpath:/assets/help.png" );
	}
}
