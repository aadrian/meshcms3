/*
 * Copyright 2004-2010 Luciano Vernaschi
 *
 * This file is part of MeshCMS.
 *
 * MeshCMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MeshCMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MeshCMS.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.cromoteca.meshcms.client.ui.pageeditor;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.FlowPanelDropController;
import com.cromoteca.meshcms.client.core.MeshCMS;
import com.cromoteca.meshcms.client.server.Module;
import com.cromoteca.meshcms.client.server.Module.Parameter;
import com.cromoteca.meshcms.client.server.Zone;
import com.cromoteca.meshcms.client.server.ZoneItem;
import com.cromoteca.meshcms.client.toolbox.Path;
import com.cromoteca.meshcms.client.ui.fields.BooleanField;
import com.cromoteca.meshcms.client.ui.fields.BooleanField.BooleanConnector;
import com.cromoteca.meshcms.client.ui.fields.Field;
import com.cromoteca.meshcms.client.ui.fields.FieldConnector;
import com.cromoteca.meshcms.client.ui.fields.FieldList;
import com.cromoteca.meshcms.client.ui.fields.IntegerField;
import com.cromoteca.meshcms.client.ui.fields.MultilineField;
import com.cromoteca.meshcms.client.ui.fields.PathField;
import com.cromoteca.meshcms.client.ui.fields.RichTextField;
import com.cromoteca.meshcms.client.ui.fields.SortedSelectionField;
import com.cromoteca.meshcms.client.ui.fields.TextField;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

public class ZoneEditorPanel extends AbsolutePanel {
	private Collection<Module> modules;
	private FieldList fields;
	private FlowPanel itemsPanel;
	private Map<Widget, ZoneItem> itemMap;
	private Path dirPath;
	private PickupDragController dragController;
	private Zone zone;

	public ZoneEditorPanel(Path dirPath, FieldList fields, Zone zone) {
		this.dirPath = dirPath;
		this.fields = fields;
		this.zone = zone;
		buildLayout();
	}

	private void buildLayout() {
		itemMap = new HashMap<Widget, ZoneItem>();
		modules = MeshCMS.SITE_INFO.getModules().values();

		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.setWidth("100%");
		scrollPanel.setHeight("100%");
		add(scrollPanel);

		FlowPanel mainPanel = new FlowPanel();
		scrollPanel.add(mainPanel);

		Field inheritableField = new BooleanField(MeshCMS.CONSTANTS
						.zoneEditorInheritable());
		fields.add(inheritableField);

		if (!Zone.HEAD_ZONE.equals(zone.getName())) {
			mainPanel.add(inheritableField);
		}

		mainPanel.add(itemsPanel = new FlowPanel());
		itemsPanel.setStylePrimaryName("mesh-zone-items");
		dragController = new PickupDragController(this, false);
		dragController.registerDropController(new FlowPanelDropController(
				itemsPanel));
		inheritableField.setConnector(new BooleanConnector() {
				@Override
				public void storeValue(boolean value) {
					zone.setInheritable(value);
				}

				@Override
				public boolean loadValue() {
					return zone.isInheritable();
				}
			});

		FlowPanel modulePanel = new FlowPanel();
		mainPanel.add(modulePanel);

		final Field moduleList = new SortedSelectionField(null) {
				@Override
				public void populateOptions(SortedMap<String, String> itemValueMap) {
					for (Module module : modules) {
						String name = module.getName();
						boolean head = Zone.HEAD_ZONE.equals(zone.getName());

						if ((head && module.isHead()) || (!head && module.isBody())) {
							itemValueMap.put(MeshCMS.getDynamicTranslation("modules_", name),
								name);
						}
					}
				}
			};

		modulePanel.add(moduleList);
		moduleList.useVoidConnector();
		modulePanel.add(new Button(MeshCMS.CONSTANTS.genericAdd(),
				new ClickHandler() {
				public void onClick(ClickEvent event) {
					ZoneItem item = new ZoneItem();
					item.setModuleName(moduleList.getValue());
					addItem(item, true, true);
				}
			}));

		for (ZoneItem item : zone.getItems()) {
			addItem(item, false, false);
		}
	}

	private void addItem(final ZoneItem item, boolean initValues, boolean opened) {
		Module m = null;

		for (Module m0 : modules) {
			if (item.getModuleName().equals(m0.getName())) {
				m = m0;

				break;
			}
		}

		final Module module = m;

		final FlowPanel draggablePanel = new FlowPanel();
		draggablePanel.setStylePrimaryName("mesh-zone-item");
		itemsPanel.add(draggablePanel);
		itemMap.put(draggablePanel, item);

		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.setStylePrimaryName("mesh-zone-item-buttons");
		draggablePanel.add(buttonsPanel);

		PushButton dragButton = new PushButton(new Image(
					MeshCMS.ICONS_BUNDLE.arrowMove()));
		dragButton.setTitle(MeshCMS.CONSTANTS.dragReorderHandlerTooltip());
		buttonsPanel.add(dragButton);

		PushButton removeButton = new PushButton(new Image(
					MeshCMS.ICONS_BUNDLE.bin()),
				new ClickHandler() {
					public void onClick(ClickEvent event) {
						fields.removeAll(draggablePanel);
						fields.verifyAll();
						itemMap.remove(draggablePanel);
						draggablePanel.removeFromParent();
					}
				});
		removeButton.setTitle(MeshCMS.CONSTANTS.genericRemove());
		buttonsPanel.add(removeButton);

		String itemLabel = MeshCMS.getDynamicTranslation("modules_",
				module.getName());
		DisclosurePanel disclosurePanel = new DisclosurePanel(itemLabel);
		disclosurePanel.setOpen(opened);
		disclosurePanel.setAnimationEnabled(true);
		draggablePanel.add(disclosurePanel);
		dragController.makeDraggable(draggablePanel, dragButton);

		FlowPanel parametersPanel = new FlowPanel();
		disclosurePanel.add(parametersPanel);

		List<Parameter> parameters = module.getParameters();

		for (final Parameter parameter : parameters) {
			if (!parameter.isSiteWide()) {
				Field field = createField(parameter, module.getName(), dirPath);

				if (field != null) {
					parametersPanel.add(field);

					FieldConnector connector = new ParameterConnector(item,
							parameter.getName());
					field.setConnector(connector);
					fields.add(field);

					if (initValues) {
						field.setValue(parameter.getDefaultValue());
					}
				}
			}
		}

		fields.verifyAll();
	}

	public void storeAll() {
		fields.storeAll();

		List<ZoneItem> items = new ArrayList<ZoneItem>();

		for (int i = 0; i < itemsPanel.getWidgetCount(); i++) {
			items.add(itemMap.get(itemsPanel.getWidget(i)));
		}

		zone.setItems(items);
	}

	public static Field createField(final Parameter parameter,
		final String moduleName, Path dirPath) {
		Field field = null;
		String label = MeshCMS.getDynamicTranslation("modules_" + moduleName
				+ "_params_", parameter.getName());

		switch (parameter.getType()) {
			case BOOLEAN:
				field = new BooleanField(label);

				break;

			case INTEGER:
				field = new IntegerField(parameter.isRequired(), label,
						IntegerField.ALL_NUMBERS);

				break;

			case MULTILINE_TEXT:
				field = new MultilineField(parameter.isRequired(), label);

				break;

			case PATH:
				field = new PathField(parameter.isRequired(), label, dirPath);

				break;

			case RICH_TEXT:
				field = new RichTextField(parameter.isRequired(), label, dirPath);

				break;

			case SELECTION:
				field = new SortedSelectionField(label) {
							@Override
							public void populateOptions(
								SortedMap<String, String> itemValueMap) {
								for (String value : parameter.getValues()) {
									itemValueMap.put(MeshCMS.getDynamicTranslation("modules_"
											+ moduleName + "_params_" + parameter.getName()
											+ "_values_", value), value);
								}
							}
						};


				break;

			case TEXT:
				field = new TextField(parameter.isRequired(), label).bigger();

				break;
		}

		return field;
	}

	private static class ParameterConnector implements FieldConnector {
		private String parameterName;
		private ZoneItem item;

		public ParameterConnector(ZoneItem item, String parameterName) {
			this.item = item;
			this.parameterName = parameterName;
		}

		public String retrieveValue() {
			return item.getParameters().get(parameterName);
		}

		public void storeValue(String value) {
			item.getParameters().put(parameterName, value);
		}
	}
}
