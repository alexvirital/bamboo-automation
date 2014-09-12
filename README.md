VeracodeAtlassianPlugin
=======================

A set of plugins to integrate Veracode with Atlassian Products.

Disclaimer
=
By using this product, you agree that you bear all risks associated with using or relying on Build.com engineered products. Build.com does not in any way warrant the accuracy, reliability, security, completeness, usefulness, non-infringement, or quality of developed products (including without limitation any applications or content contained therein), regardless of who originated that content (including Build.com employees, partners, or affiliates). Build.com hereby disclaim all warranties, including but not limited to any implied warranties of title, non-infringement, merchantability or fitness for a particular purpose, relating to such Marketplace Products. Build.com shall not be liable or responsible in any way for any losses or damage of any kind, including lost profits or other indirect or consequential damages, relating to your use of or reliance upon any Build.com Marketplace Products in the Atlassian Marketplace.

Indemnification. You agree to indemnify and hold Build.com and its subsidiaries, affiliates, officers, agents, and employees harmless from any claims by third parties, and any related damages, losses or costs (including reasonable attorney fees and costs), arising out of content you use from the Atlassian Marketplace.

Usage
=
You'll need to install the Atlassian SDK. To package the plugin, run the following command from within the bamboo/VeracodePlugin directory.

    atlas-package

Once the command is complete, there will be a jar file located in bamboo/VeracodePlugin/target. Using the plugin manager in Bamboo, choose Upload Plugin, and when prompted, browse and select the jar file created using the atlas-package command. Once the plugin is installed, there will be two additional tasks available when configuring a plan. These tasks are called VeracodeUpload and VeracodeResults.
