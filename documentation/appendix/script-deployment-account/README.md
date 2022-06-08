# Fast Path to Account Setup
## Automate account setup with bash scripts, CF CLI and BTP CLI

## Prerequisites

* Supported Systems: Linux / Mac / Ubuntu on Windows
* Bash, ability to modify $PATH, install additional software
* BPT CLI Client: https://tools.hana.ondemand.com/
* CF CLI client: https://docs.cloudfoundry.org/cf-cli/install-go-cli.html
* JQ: ```sudo apt-get install jq```
* (Optional) VS Code
* (Optional) Shellcheck: https://github.com/vscode-shellcheck/vscode-shellcheck

The two optional components are useful if you want to understand the scripts in detail or modify them

## Create Pay-As-You-Go account

This setup steps are for completely new users. If you already have an account some steps may differ. In that case please make sure you have the right account type and entitlements, otherwise the automated account setup may fail.

* Visit SAP Store at https://store.sap.com/dcp/en/
* In **Browse** Menu sleect **SAP Business Technology Platform**, then **All in SAP Business Technology Platform**
* You should find the card **Pay-AsYou-Go for SAP BTP**. After clicking it you see different options for Plans and Pricing. Add the plan **Gert started free** to your cart.
* You can choose length of the renewal cycle and continue to checkout, where you have to enter pyament details if not already stored on your account.
* Make sure you anderstand that this account only is free as long as you do not book any non-free services! Following our instructions on this page will only activate free services.
* After you received an email that your account is ready to use, go to SAP BTP Cockpit at https://account.hana.ondemand.com/ and sign in.
* If there are multiple accounts assotiated with your user you will see a choice to select one.

## Usage

Start a shell and go to the [setup folder](/code/setup).

Please make sure that you have installed btp and cf client and that both applications can be found. The script will check that the clients are available.

Our script will create the following items under your Global Account after you provided all the needed input.
* directory
* subaccount
* kyma and hana entitlements
* kyma and cf enabling
* hana free-plan creation in cf space

Execute script:

```shell
./account_setup.sh
```

### Client Login

If you are unsure which URLs to use for login with btp or cf the following overview might help. It depends on your Global Account

#### BTP Login
* Canary: https://cpcli.cf.sap.hana.ondemand.com
* all others: https://cpcli.cf.eu10.hana.ondemand.com

#### CF Login

You do not need to select an Org after logging into cf. Just skip this question!

* Canary: https://api.cf.sap.hana.ondemand.com/
* EU20: https://api.cf.eu20.hana.ondemand.com
* EU10: https://api.cf.eu10.hana.ondemand.com
* US10: https://api.cf.us10.hana.ondemand.com

## Further Info

* BTP CLI docu: https://help.sap.com/products/BTP/65de2977205c403bbc107264b8eccf4b/7c6df2db6332419ea7a862191525377c.html
