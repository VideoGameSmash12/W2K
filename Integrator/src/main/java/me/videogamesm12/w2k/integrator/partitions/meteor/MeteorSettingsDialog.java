package me.videogamesm12.w2k.integrator.partitions.meteor;

import me.videogamesm12.w2k.blackbox.Blackbox;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.systems.config.Config;

import javax.swing.*;
import java.awt.*;

public class MeteorSettingsDialog extends JDialog
{
	public MeteorSettingsDialog()
	{
		super(Blackbox.getInstance().getMainWindow(), MeteorClient.NAME + " Settings");

		BoxLayout pLayout = new BoxLayout(getContentPane(), BoxLayout.Y_AXIS);
		setLayout(pLayout);

		Config.get().settings.groups.forEach(group ->
		{
			final JPanel panel = new JPanel();
			panel.setBorder(BorderFactory.createTitledBorder(group.name));

			GroupLayout layout = new GroupLayout(panel);
			layout.setAutoCreateGaps(true);
			layout.setAutoCreateContainerGaps(true);
			panel.setLayout(layout);

			GroupLayout.Group labels = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
			GroupLayout.Group settings = layout.createParallelGroup(GroupLayout.Alignment.LEADING);

			GroupLayout.SequentialGroup vertical = layout.createSequentialGroup();

			for (Setting<?> setting : group)
			{
				final JLabel settingLabel = new JLabel(setting.title);
				final JComponent settingComponent = MeteorModuleSettingsDialog.getSettingComponent(this, setting);

				labels.addComponent(settingLabel);
				settings.addComponent(settingComponent);

				vertical.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(settingLabel)
						.addComponent(settingComponent));
			}

			layout.setHorizontalGroup(layout.createSequentialGroup()
					.addGroup(labels).addGroup(settings));
			layout.setVerticalGroup(vertical);

			add(panel);
		});

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		setMinimumSize(new Dimension(getContentPane().getWidth(), getContentPane().getHeight()));
		setResizable(false);

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(dim.width/2 - getSize().width/2, dim.height/2 - getSize().height/2);

		pack();
	}
}
