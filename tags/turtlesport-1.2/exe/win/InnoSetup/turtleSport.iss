; Script generated by the Inno Setup Script Wizard.
; SEE THE DOCUMENTATION FOR DETAILS ON CREATING INNO SETUP SCRIPT FILES!

[Setup]
; NOTE: The value of AppId uniquely identifies this application.
; Do not use the same AppId value in installers for other applications.
; (To generate a new GUID, click Tools | Generate GUID inside the IDE.)
AppId={{14B614CA-42E0-4545-A272-B1248809E95D}
AppName=Turtle Sport v1.2
AppVerName=Turtle Sport v1.2
AppVersion=1.2
AppPublisher=TurtleSport
AppPublisherURL=http://turtlesport.sourceforge.net
AppSupportURL=http://turtlesport.sourceforge.net
AppUpdatesURL=http://turtlesport.sourceforge.net
DefaultDirName={pf}\turtlesport
DefaultGroupName=Turtle Sport
OutputBaseFilename=turtlesport-win-1.2
Compression=lzma
SolidCompression=yes
WizardSmallImageFile=Turtle-55.bmp
WizardImageFile=wizardImage2.bmp
LicenseFile=lgpl-2.1.txt
SetupIconFile=turtleSport.ico

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"
Name: "french"; MessagesFile: "compiler:Languages\French.isl"
Name: "german"; MessagesFile: "compiler:Languages\German.isl"
Name: "spanish"; MessagesFile: "compiler:Languages\Spanish.isl"
Name: "catalan"; MessagesFile: "compiler:Languages\Catalan.isl"
Name: "Hungarian"; MessagesFile: "compiler:Languages\Hungarian.isl"
Name: "swedish"; MessagesFile: "compiler:Languages\Swedish.isl"
Name: "italian"; MessagesFile: "compiler:Languages\Italian.isl"
Name: "nederlands"; MessagesFile: "compiler:Languages\Dutch.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked
Name: "quicklaunchicon"; Description: "{cm:CreateQuickLaunchIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked

[Files]
Source: "..\derby.jar"; DestDir: "{app}"; Flags: ignoreversion
Source: "..\fit.jar"; DestDir: "{app}"; Flags: ignoreversion
Source: "..\jcommon.jar"; DestDir: "{app}"; Flags: ignoreversion
Source: "..\jfreechart.jar"; DestDir: "{app}"; Flags: ignoreversion
Source: "..\log4j.jar"; DestDir: "{app}"; Flags: ignoreversion
Source: "..\log4J.xml"; DestDir: "{app}"; Flags: ignoreversion
Source: "..\msvcr71.dll"; DestDir: "{app}"; Flags: ignoreversion
Source: "..\swingx-ws.jar"; DestDir: "{app}"; Flags: ignoreversion
Source: "..\swingx.jar"; DestDir: "{app}"; Flags: ignoreversion
Source: "..\turtleMail.dll"; DestDir: "{app}"; Flags: ignoreversion
Source: "..\turtleRegistryWin.dll"; DestDir: "{app}"; Flags: ignoreversion
Source: "..\turtlesport.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "..\turtlesport.jar"; DestDir: "{app}"; Flags: ignoreversion
Source: "..\turtleUsbjni.dll"; DestDir: "{app}"; Flags: ignoreversion
; NOTE: Don't use "Flags: ignoreversion" on any shared system files

[Icons]
Name: "{group}\Turtle Sport"; Filename: "{app}\turtlesport.exe"
Name: "{group}\{cm:ProgramOnTheWeb,Turtle Sport}"; Filename: "http://turtlesport.sourceforge.net"
Name: "{group}\{cm:UninstallProgram,Turtle Sport}"; Filename: "{uninstallexe}"
Name: "{commondesktop}\Turtle Sport"; Filename: "{app}\turtlesport.exe"; Tasks: desktopicon
Name: "{userappdata}\Microsoft\Internet Explorer\Quick Launch\Turtle Sport"; Filename: "{app}\turtlesport.exe"; Tasks: quicklaunchicon

[Run]
Filename: "{app}\turtlesport.exe"; Description: "{cm:LaunchProgram,Turtle Sport}"; Flags: nowait postinstall skipifsilent
