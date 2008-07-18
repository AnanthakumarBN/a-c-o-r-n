/*
 * Created on Feb 8, 2007
 *
 */
package com.srijeeb.jsf.tags;

import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.component.UIComponent;
import javax.faces.el.ValueBinding;
import javax.faces.webapp.UIComponentTag;

import com.srijeeb.jsf.components.UICustomSelectOneRadio;

/**
 * @author srijeeb
 *
 */
public class HTMLCustomSelectOneRadioTag extends UIComponentTag {

	/* (non-Javadoc)
	 * @see javax.faces.webapp.UIComponentTag#getComponentType()
	 */
	public String getComponentType() {
		return "component.CustomSelectOneRadio";
	}

	/* (non-Javadoc)
	 * @see javax.faces.webapp.UIComponentTag#getRendererType()
	 */
	public String getRendererType() {
		return "renderer.CustomSelectOneRadio";
	}

	private String name = null;
	private String value = null;
	private String styleClass = null;
	private String style = null;
	private String disabled = null;
	private String itemLabel = null;
	private String itemValue = null;
	private String onClick = null;
	private String onMouseOver = null;
	private String onMouseOut = null;
	private String onFocus = null;
	private String onBlur = null;
	private String overrideName = null;
	
	
	/**
	 * @return
	 */
	public String getDisabled() {
		return disabled;
	}

	/**
	 * @return
	 */
	public String getItemLabel() {
		return itemLabel;
	}

	/**
	 * @return
	 */
	public String getItemValue() {
		return itemValue;
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return
	 */
	public String getOnBlur() {
		return onBlur;
	}

	/**
	 * @return
	 */
	public String getOnClick() {
		return onClick;
	}

	/**
	 * @return
	 */
	public String getOnFocus() {
		return onFocus;
	}

	/**
	 * @return
	 */
	public String getOnMouseOut() {
		return onMouseOut;
	}

	/**
	 * @return
	 */
	public String getOnMouseOver() {
		return onMouseOver;
	}

	/**
	 * @return
	 */
	public String getOverrideName() {
		return overrideName;
	}

	/**
	 * @return
	 */
	public String getStyle() {
		return style;
	}

	/**
	 * @return
	 */
	public String getStyleClass() {
		return styleClass;
	}

	/**
	 * @return
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param string
	 */
	public void setDisabled(String string) {
		disabled = string;
	}

	/**
	 * @param string
	 */
	public void setItemLabel(String string) {
		itemLabel = string;
	}

	/**
	 * @param string
	 */
	public void setItemValue(String string) {
		itemValue = string;
	}

	/**
	 * @param string
	 */
	public void setName(String string) {
		name = string;
	}

	/**
	 * @param string
	 */
	public void setOnBlur(String string) {
		onBlur = string;
	}

	/**
	 * @param string
	 */
	public void setOnClick(String string) {
		onClick = string;
	}

	/**
	 * @param string
	 */
	public void setOnFocus(String string) {
		onFocus = string;
	}

	/**
	 * @param string
	 */
	public void setOnMouseOut(String string) {
		onMouseOut = string;
	}

	/**
	 * @param string
	 */
	public void setOnMouseOver(String string) {
		onMouseOver = string;
	}

	/**
	 * @param string
	 */
	public void setOverrideName(String string) {
		overrideName = string;
	}

	/**
	 * @param string
	 */
	public void setStyle(String string) {
		style = string;
	}

	/**
	 * @param string
	 */
	public void setStyleClass(String string) {
		styleClass = string;
	}

	/**
	 * @param string
	 */
	public void setValue(String string) {
		value = string;
	}

	protected void setProperties(UIComponent component) {
		super.setProperties(component);

		UICustomSelectOneRadio aUICustomSelectOneRadio 
			= (UICustomSelectOneRadio) component;

		if (name != null) {
			if (isValueReference(name)) {
				aUICustomSelectOneRadio.setValueBinding("name", getValueBinding(name));
			} else {
				aUICustomSelectOneRadio.getAttributes()
					.put("name", name);
			}
		}

		if (value != null) {
			if (isValueReference(value)) {
				aUICustomSelectOneRadio.setValueBinding("value", getValueBinding(value));
			} else {
				aUICustomSelectOneRadio.getAttributes()
					.put("value", value);
			}
		}		
		if (styleClass != null) {
			if (isValueReference(styleClass)) {
				aUICustomSelectOneRadio.setValueBinding("styleClass", getValueBinding(styleClass));
			} else {
				aUICustomSelectOneRadio.getAttributes()
					.put("styleClass", styleClass);
			}
		}
		if (style != null) {
			if (isValueReference(style)) {
				aUICustomSelectOneRadio.setValueBinding("style", getValueBinding(style));
			} else {
				aUICustomSelectOneRadio.getAttributes()
					.put("style", style);
			}
		}
		if (disabled != null) {
			if (isValueReference(disabled)) {
				aUICustomSelectOneRadio.setValueBinding("disabled", getValueBinding(disabled));
			} else {
				aUICustomSelectOneRadio.getAttributes()
					.put("disabled", disabled);
			}
		}
		if (itemLabel != null) {
			if (isValueReference(itemLabel)) {
				aUICustomSelectOneRadio.setValueBinding("itemLabel", getValueBinding(itemLabel));
			} else {
				aUICustomSelectOneRadio.getAttributes()
					.put("itemLabel", itemLabel);
			}
		}
		if (itemValue != null) {
			if (isValueReference(itemValue)) {
				aUICustomSelectOneRadio.setValueBinding("itemValue", getValueBinding(itemValue));
			} else {
				aUICustomSelectOneRadio.getAttributes()
					.put("itemValue", itemValue);
			}
		}		
		if (onClick != null) {
			if (isValueReference(onClick)) {
				aUICustomSelectOneRadio.setValueBinding("onClick", getValueBinding(onClick));
			} else {
				aUICustomSelectOneRadio.getAttributes()
					.put("onClick", onClick);
			}
		}		
		if (onMouseOver != null) {
			if (isValueReference(onMouseOver)) {
				aUICustomSelectOneRadio.setValueBinding("onMouseOver", getValueBinding(onMouseOver));
			} else {
				aUICustomSelectOneRadio.getAttributes()
					.put("onMouseOver", onMouseOver);
			}
		}		
		if (onMouseOut != null) {
			if (isValueReference(onMouseOut)) {
				aUICustomSelectOneRadio.setValueBinding("onMouseOut", getValueBinding(onMouseOut));
			} else {
				aUICustomSelectOneRadio.getAttributes()
					.put("onMouseOut", onMouseOut);
			}
		}		
		if (onFocus != null) {
			if (isValueReference(onFocus)) {
				aUICustomSelectOneRadio.setValueBinding("onFocus", getValueBinding(onFocus));
			} else {
				aUICustomSelectOneRadio.getAttributes()
					.put("onFocus", onFocus);
			}
		}			
		if (onBlur != null) {
			if (isValueReference(onBlur)) {
				aUICustomSelectOneRadio.setValueBinding("onBlur", getValueBinding(onBlur));
			} else {
				aUICustomSelectOneRadio.getAttributes()
					.put("onBlur", onBlur);
			}
		}

		if (overrideName != null) {
			if (isValueReference(overrideName)) {
				aUICustomSelectOneRadio.setValueBinding("overrideName", getValueBinding(overrideName));
			} else {
				aUICustomSelectOneRadio.getAttributes()
					.put("overrideName", overrideName);
			}
		}		
	}
	public ValueBinding getValueBinding(String valueRef) {
		ApplicationFactory af =
			(ApplicationFactory) FactoryFinder.getFactory(FactoryFinder.APPLICATION_FACTORY);
		Application a = af.getApplication();

		return (a.createValueBinding(valueRef));
	}
}
