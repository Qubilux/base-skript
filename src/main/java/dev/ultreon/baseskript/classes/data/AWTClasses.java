/**
 *   This file is part of Skript.
 *
 *  Skript is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Skript is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Skript.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright Peter Güttinger, SkriptLang team and contributors
 */
package dev.ultreon.baseskript.classes.data;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.registrations.Classes;

import javax.swing.*;

public class AWTClasses {
	static {
		Classes.registerClass(new ClassInfo<JComponent>(JComponent.class, "jcomponent")
				.user("(widget component|JComponent)s?")
				.name("AWT Component")
				.description("Any widget component.")
				.usage("")
				.examples("")
				.since("3.0"));
		
		Classes.registerClass(new ClassInfo<JPanel>(JPanel.class, "jpanel")
				.user("(pane [widget [component]]|JPanel)s?")
				.name("AWT Panel")
				.description("A panel widget component.")
				.usage("")
				.examples("set {panel} to a new panel in {window}")
				.since("3.0"));
		
		Classes.registerClass(new ClassInfo<JButton>(JButton.class, "jbutton")
				.user("(button [widget [component]]|JButton)s?")
				.name("AWT Button")
				.description("A button widget component.")
				.usage("set {button} to a new button in {panel}")
				.examples("")
				.since("3.0"));

		Classes.registerClass(new ClassInfo<JLabel>(JLabel.class, "jlabel")
				.user("(label [widget [component]]|JLabel)s?")
				.name("AWT Label")
				.description("A label widget component.")
				.usage("set {label} to a new label in {panel}")
				.examples("")
				.since("3.0"));

		Classes.registerClass(new ClassInfo<JTextField>(JTextField.class, "jtextfield")
				.user("(text field [widget [component]]|JTextField)s?")
				.name("AWT Text Field")
				.description("A text field widget component.")
				.usage("set {text-field} to a new text field in {panel}")
				.examples("")
				.since("3.0"));

		Classes.registerClass(new ClassInfo<JCheckBox>(JCheckBox.class, "jcheckbox")
				.user("(check box [widget [component]]|JCheckBox)s?")
				.name("AWT Check Box")
				.description("A check box widget component.")
				.usage("set {check-box} to a new check box in {panel}")
				.examples("")
				.since("3.0"));

		Classes.registerClass(new ClassInfo<JRadioButton>(JRadioButton.class, "jradiobutton")
				.user("(radio [button] [widget [component]]|JRadioButton)s?")
				.name("AWT Radio Button")
				.description("A radio button widget component.")
				.usage("set {radio-button} to a new radio button in {panel}")
				.examples("")
				.since("3.0"));

		Classes.registerClass(new ClassInfo<JComboBox>(JComboBox.class, "jcombobox")
				.user("((combo box|combobox) [widget [component]]|JComboBox)s?")
				.name("AWT Combo Box")
				.description("A combo box widget component.")
				.usage("set {combo-box} to a new combo box in {panel}")
				.examples("")
				.since("3.0"));

		Classes.registerClass(new ClassInfo<JList>(JList.class, "jlist")
				.user("(list widget [component]|JList)s?")
				.name("AWT List")
				.description("A list widget component.")
				.usage("set {list} to a new list in {panel}")
				.examples("")
				.since("3.0"));

		Classes.registerClass(new ClassInfo<JTable>(JTable.class, "jtable")
				.user("(table widget [component]|JTable)s?")
				.name("AWT Table")
				.description("A table widget component.")
				.usage("set {table} to a new table in {panel}")
				.examples("")
				.since("3.0"));

		Classes.registerClass(new ClassInfo<JScrollPane>(JScrollPane.class, "jscrollpane")
				.user("(scroll (pane|panel|area|view) [widget [component]]|JScrollPane)s?")
				.name("AWT Scroll Pane")
				.description("A scroll pane widget component.")
				.usage("set {scroll-pane} to a new scroll pane in {panel}")
				.examples("")
				.since("3.0"));

		Classes.registerClass(new ClassInfo<JTextArea>(JTextArea.class, "jtextarea")
				.user("(text area [widget [component]]|JTextArea)s?")
				.name("AWT Text Area")
				.description("A text area widget component.")
				.usage("set {text-area} to a new text area in {panel}")
				.examples("")
				.since("3.0"));

		Classes.registerClass(new ClassInfo<JTabbedPane>(JTabbedPane.class, "jtabbedpane")
				.user("(tabbed pane [widget [component]]|JTabbedPane)s?")
				.name("AWT Tabbed Pane")
				.description("A tabbed pane widget component.")
				.usage("set {tabbed-pane} to a new tabbed pane in {panel}")
				.examples("")
				.since("3.0"));

		Classes.registerClass(new ClassInfo<JProgressBar>(JProgressBar.class, "jprogressbar")
				.user("((progress bar|progressbar) [widget [component]|JProgressBar])s?")
				.name("AWT Progress Bar")
				.description("A progress bar widget component.")
				.usage("set {progress-bar} to a new progress bar in {panel}")
				.examples("")
				.since("3.0"));

		Classes.registerClass(new ClassInfo<JSlider>(JSlider.class, "jslider")
				.user("([number] slider [widget [component]|JSlider])s?")
				.name("AWT Slider")
				.description("A slider widget component.")
				.usage("set {slider} to a new slider in {panel}")
				.examples("")
				.since("3.0"));

		Classes.registerClass(new ClassInfo<JSpinner>(JSpinner.class, "jspinner")
				.user("((spinner|number chooser) [widget [component]|JSpinner])s?")
				.name("AWT Spinner")
				.description("A spinner widget component.")
				.usage("set {spinner} to a new spinner in {panel}")
				.examples("")
				.since("3.0"));
	}
}
