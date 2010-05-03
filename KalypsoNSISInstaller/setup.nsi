# Auto-generated by EclipseNSIS Script Wizard
# 20.06.2006 11:30:12

# Der Name des Projektes.
Name Kalypso

# Definitionen.
!define REGKEY "SOFTWARE\$(^Name)"

# version number, which is used for builtin pathes and menu entries.
!define VERSION 2.2.0.RC2
!define DATE 20100312

!define COMPANY BCE
!define URL http://kalypso.bjoernsen.de

# MUI Interface Einstellungen (muss vor den Page-Makros sein).
!define MUI_ABORTWARNING
;!define MUI_ICON "${NSISDIR}\Contrib\Graphics\Icons\modern-install.ico"
!define MUI_ICON "data\kalypso_ico_32.ico"
!define MUI_FINISHPAGE_NOAUTOCLOSE
!define MUI_STARTMENUPAGE_REGISTRY_ROOT HKLM
!define MUI_STARTMENUPAGE_NODISABLE
!define MUI_STARTMENUPAGE_REGISTRY_KEY Software\Kalypso
!define MUI_STARTMENUPAGE_REGISTRY_VALUENAME StartMenuGroup
!define MUI_STARTMENUPAGE_DEFAULT_FOLDER Kalypso
;!define MUI_UNICON "${NSISDIR}\Contrib\Graphics\Icons\modern-uninstall.ico"
!define MUI_UNICON "data\kalypso_ico_32.ico"
!define MUI_UNFINISHPAGE_NOAUTOCLOSE
!define MUI_CUSTOMPAGECOMMANDS
!define MUI_CUSTOMPAGEUNCOMMANDS

# MUI Definitionen.
!define MUI_LANGDLL_REGISTRY_ROOT HKLM
!define MUI_LANGDLL_REGISTRY_KEY ${REGKEY}
!define MUI_LANGDLL_REGISTRY_VALUENAME InstallerLanguage

# Included Files.
!include Sections.nsh
!include MUI.nsh
!include LogicLib.nsh

# Reserved Files.
ReserveFile page_data.ini
ReserveFile "${NSISDIR}\Plugins\AdvSplash.dll"
!insertmacro MUI_RESERVEFILE_LANGDLL

# Variables (only used during the installation).

# In this variable the start menu group selected in the start menu page will be stored.
Var StartMenuGroup

# Variables (only used durning the uninstallation).

# Variables (used during installation and uninstallation).
Var startmenu_path
Var desktop_icon
Var quicklaunch_icon
Var startmenu_icons

# This variable is a flag and stores TRUE, if a critical path was entered.
Var critical_paths

# Variable for install subfolder
var sub_folder

# Die anzuzeigenden Seiten des Installers (sie werden in dieser Reihenfolge angezeigt).
!insertmacro MUI_PAGE_WELCOME

# !insertmacro MUI_PAGE_LICENSE license.txt
!insertmacro MUI_PAGE_LICENSE install_data/license/license.txt

!DEFINE MUI_PAGE_CUSTOMFUNCTION_PRE pre_DirPage
!DEFINE MUI_PAGE_CUSTOMFUNCTION_LEAVE leave_DirPage
!insertmacro MUI_PAGE_DIRECTORY

# Die eigens erstellte Seite f�r die Optionen.
page custom cb_PageData leave_DataPage

!DEFINE MUI_PAGE_CUSTOMFUNCTION_PRE pre_StartmenuPage
!insertmacro MUI_PAGE_STARTMENU Application $StartMenuGroup

!insertmacro MUI_PAGE_INSTFILES

!insertmacro MUI_PAGE_FINISH

# Die anzuzeigenden Seiten des Uninstallers (sie werden in dieser Reihenfolge angezeigt).
!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES

# Installer Sprachen.
!insertmacro MUI_LANGUAGE English
!insertmacro MUI_LANGUAGE German

# Installer - Attribute.
OutFile "setup_kalypso_${VERSION}_${Date}.exe"
InstallDir $PROGRAMFILES\Kalypso
CRCCheck on
XPStyle on
ShowInstDetails hide
VIProductVersion 2.2.0.20100312

VIAddVersionKey /LANG=${LANG_GERMAN} ProductVersion "${VERSION}"
VIAddVersionKey /LANG=${LANG_GERMAN} ProductName Kalypso
VIAddVersionKey /LANG=${LANG_GERMAN} CompanyName "${COMPANY}"
VIAddVersionKey /LANG=${LANG_GERMAN} CompanyWebsite "${URL}"
VIAddVersionKey /LANG=${LANG_GERMAN} FileVersion ""
VIAddVersionKey /LANG=${LANG_GERMAN} FileDescription ""
VIAddVersionKey /LANG=${LANG_GERMAN} LegalCopyright ""

VIAddVersionKey /LANG=${LANG_ENGLISH} ProductVersion "${VERSION}"
VIAddVersionKey /LANG=${LANG_ENGLISH} ProductName Kalypso
VIAddVersionKey /LANG=${LANG_ENGLISH} CompanyName "${COMPANY}"
VIAddVersionKey /LANG=${LANG_ENGLISH} CompanyWebsite "${URL}"
VIAddVersionKey /LANG=${LANG_ENGLISH} FileVersion ""
VIAddVersionKey /LANG=${LANG_ENGLISH} FileDescription ""
VIAddVersionKey /LANG=${LANG_ENGLISH} LegalCopyright ""

ShowUninstDetails hide

# Eine mehrsprachige Warnmeldung, wenn das Verzeichnis bereits existiert.
LangString warndir ${LANG_ENGLISH} "The directory you have choosen already exists. Existing files will be overwritten. Do you still want to continue?"
LangString warndir ${LANG_GERMAN} "Das Verzeichnis, das Sie ausgew�hlt haben existiert bereits. Existierende Dateien werden �berschrieben. M�chten Sie dennoch fortfahren?"

# Eine mehrsprachige Warnmeldung, wenn es bereits eine Installation in diesem Verzeichnis gibt.
LangString warninst ${LANG_ENGLISH} "Found Kalypso in this folder. You should delete it first. Continuing will overwrite existing files. Do you still want to continue?"
LangString warninst ${LANG_GERMAN} "Kalypso in diesem Verzeichnis gefunden. Sie sollten es zun�chst l�schen. Wenn Sie fortfahren werden existierende Dateien �berschrieben. M�chten Sie dennoch fortfahren?"

# Eine mehrsprachige Warnmeldung, wenn es keine Installation in dem Ordner gibt, in dem Uninstall aufgerufen wurde.
LangString warninst2 ${LANG_ENGLISH} "In this folder exists no correct copy of Kalypso. Aborting ..."
LangString warninst2 ${LANG_GERMAN} "In diesem Verzeichnis existiert keine korrekte Kopie von Kalypso. Abbruch ..."

# Eine mehrsprachige Warnmeldung, dass ein kritischer Pfad ausgew�hlt wurde.
LangString warninst3 ${LANG_ENGLISH} "You can't select the directories: $PROGRAMFILES, $WINDIR"
LangString warninst3 ${LANG_GERMAN} "Die Verzeichnisse k�nnen nicht gew�hlt werden: $PROGRAMFILES, $WINDIR"

# Eine mehrsprachige Warnmeldung, dass ein kritischer Pfad gel�scht werden soll.
LangString warninst4 ${LANG_ENGLISH} "The directories $PROGRAMFILES, $WINDIR cannot be deleted. Abort ..."
LangString warninst4 ${LANG_GERMAN} "Die Verzeichnisse $PROGRAMFILES, $WINDIR k�nnen nicht gel�scht werden. Abbruch ..."

# Eine mehrsprachige Warnmeldung, wenn die kalypso_uninstall.ini nicht gefunden wurde.
LangString noini ${LANG_ENGLISH} "Configuration of the delete assistant not found. Aborting ..."
LangString noini ${LANG_GERMAN} "Die Konfiguration des L�sch-Assistenten wurde nicht gefunden. Abbruch ..."

# Eine mehrsprachige Warnmeldung, falls ein Wert aus der INI nicht gelesen werden konnte.
LangString errini ${LANG_ENGLISH} "The configuration of the delete assistant is not valid. Aborting ..."
LangString errini ${LANG_GERMAN} "Die Konfiguration des L�sch-Assistenten ist nicht g�ltig. Abbruch ..."

# Anzeige f�r die selbstgebaute Optionen-Seite.
LangString opt_title ${LANG_ENGLISH} "Options"
LangString opt_title ${LANG_GERMAN} "Optionen"

LangString opt_text ${LANG_ENGLISH} "Please select your options."
LangString opt_text ${LANG_GERMAN} "Bitte w�hlen Sie Ihre Optionen."

LangString opt_page1 ${LANG_ENGLISH} "Add icon to desktop"
LangString opt_page1 ${LANG_GERMAN} "Icon auf dem Desktop erstellen"

LangString opt_page2 ${LANG_ENGLISH} "Add icon to quickstart"
LangString opt_page2 ${LANG_GERMAN} "Icon in der Schnellstart-Leiste erstellen"

LangString opt_page3 ${LANG_ENGLISH} "Add icons to startmenu"
LangString opt_page3 ${LANG_GERMAN} "Icons im Startmen� erzeugen"

# Fehler, falls beim �ndern der config.ini etwas schief gelaufen ist.
LangString err1 ${LANG_ENGLISH} "Error: The neccessary changes to the configuration/config.ini or kalypso_uninstall.ini could not be made."
LangString err1 ${LANG_GERMAN} "Fehler: Die notwendigen �nderungen an der configuration/config.ini oder kalypso_uninstall.ini konnten nicht durchgef�hrt werden."

# Die Sektionen.
Section Kalypso SEC0000
    SetOverwrite On
    SetOutPath $INSTDIR\$sub_folder

    # Path to your deploy folder
    File /r /x .svn install_data\kalypso\*
SectionEnd

# Diese Sektion ist versteckt (wegen dem -).
# Alle Sektionen, die ausgew�hlt sind, werden ausgef�hrt.
# Da diese Sektion versteckt und ausgew�hlt ist, kann sie nicht deselektiert werden,
# wird also immer ausgef�hrt. Eine Sektion kann Dateien kopieren (installieren), oder aber,
# wie in diesem Fall lediglich Registry�nderungen vornehmen.
# Da diese �nderungen gemacht werden m�ssen, ist diese Sektion versteckt.
Section -post SEC0001
    SectionGetFlags ${SEC0000} $0
    IntOp $0 $0 & ${SF_SELECTED}

    # Ist die kalypso-Section zur Installation ausgew�hlt,
    # so lege Icons usw. an.
    ${If} $0 == "1"
        WriteUninstaller $INSTDIR\$sub_folder\uninstall.exe"

        # Startmen�
        ${If} $startmenu_icons == "1"
          !insertmacro MUI_STARTMENU_WRITE_BEGIN Application
          SetOutPath $SMPROGRAMS\$StartMenuGroup\$sub_folder
          SetOutPath $INSTDIR\$sub_folder
          CreateShortcut "$SMPROGRAMS\$StartMenuGroup\$sub_folder\$sub_folder.lnk" $INSTDIR\$sub_folder\kalypso.exe
          CreateShortcut "$SMPROGRAMS\$StartMenuGroup\$sub_folder\$(^UninstallLink).lnk" $INSTDIR\$sub_folder\uninstall.exe
          !insertmacro MUI_STARTMENU_WRITE_END
        ${EndIf}

        # Desktop
        ${If} $desktop_icon == "1"
          CreateShortcut "$DESKTOP\$sub_folder.lnk" $INSTDIR\$sub_folder\kalypso.exe
        ${EndIf}

        # Schnellstart
        ${If} $quicklaunch_icon == "1"
          CreateShortcut "$QUICKLAUNCH\$sub_folder.lnk" $INSTDIR\$sub_folder\kalypso.exe
        ${EndIf}

        ClearErrors

;        # Config-File f�r kalypso bearbeiten.
;        FileOpen $0 $INSTDIR\kalypso\configuration\config.ini a
;        IfErrors error
;        FileSeek $0 0 END
;        FileWrite $0 "osgi.instance.area=$R0"
;        FileClose $0
;        IfErrors error

;        # ini-File f�r kalypso bearbeiten.
;        FileOpen $0 $INSTDIR\kalypso\kalypso.ini a
;        IfErrors error
;        FileSeek $0 0 END
;        FileWrite $0 "-nl"
;        FileWrite $0 "de_DE"
;        FileWrite $0 "-vmargs"
;        FileWrite $0 "-Xmx1024m"
;        FileWrite $0 "-XX:PermSize=64M"
;        FileWrite $0 "-XX:MaxPermSize=128M"
;        FileClose $0
;        IfErrors error

        # Config-File f�r den Uninstaller erstellen.
        WriteINIStr "$INSTDIR\$sub_folder\kalypso_uninstall.ini" "Kalypso" "Path" $INSTDIR
        WriteINIStr "$INSTDIR\$sub_folder\kalypso_uninstall.ini" "Startmenu" "Path" $SMPROGRAMS\$StartMenuGroup
        WriteINIStr "$INSTDIR\$sub_folder\kalypso_uninstall.ini" "Icons" "Desktop" $desktop_icon
        WriteINIStr "$INSTDIR\$sub_folder\kalypso_uninstall.ini" "Icons" "Quicklaunch" $quicklaunch_icon
        WriteINIStr "$INSTDIR\$sub_folder\kalypso_uninstall.ini" "Icons" "Startmenu" $startmenu_icons
        IfErrors error
        GoTo done

        error:
          MessageBox MB_OK|MB_ICONEXCLAMATION $(err1)

        done:
    ${EndIf}
SectionEnd

# Uninstaller sections
Section /o un.Main UNSEC0000
    RmDir /r /REBOOTOK "$INSTDIR\$sub_folder\*"

    # Das Verzeichnis $INSTDIR wird extra gel�scht, um zu verhindern, dass es,
    # wenn es nicht leer und evt. ein kritisches Verzeichnis ist, gel�scht wird.
    RmDir "$INSTDIR"
SectionEnd

Section un.post UNSEC0001
    # Startmen�
    ${If} $startmenu_icons == "1"
      Delete /REBOOTOK "$startmenu_path\$sub_folder\$sub_folder.lnk"
      Delete /REBOOTOK "$startmenu_path\$sub_folder\$(^UninstallLink).lnk"
      Delete /REBOOTOK "$INSTDIR\$sub_folder\uninstall.exe"
    ${EndIf}

    # Desktop
    ${If} $desktop_icon == "1"
      Delete /REBOOTOK "$DESKTOP\$sub_folder.lnk"
    ${EndIf}

    # Schnellstart
    ${If} $quicklaunch_icon == "1"
      Delete /REBOOTOK "$QUICKLAUNCH\$sub_folder.lnk"
    ${EndIf}

    # Startmen�-Pfad
    ${If} $startmenu_icons == "1"
      RmDir /REBOOTOK "$startmenu_path\$sub_folder"
      RmDir /REBOOTOK "$startmenu_path"
    ${EndIf}
SectionEnd

# Installer Funktionen.
Function .onInit
    System::Call 'kernel32::CreateMutexA(i 0, i 0, t "Kalypso-Mutex") i .r1 ?e'
    Pop $R0

    StrCmp $R0 0 +3
      MessageBox MB_OK|MB_ICONEXCLAMATION "The installer is already running."
      Abort

    # Plugin-Verzeichnis initialisieren.
    InitPluginsDir

    # Seite zur Auswahl des Daten-Verzeichnisses.
    File /oname=$PLUGINSDIR\page_data.ini "page_data.ini"

    # Splash-Screen anzeigen.
    Push $R1
    File /oname=$PLUGINSDIR\spltmp.bmp data\kalypso_logo.bmp
    advsplash::show 1000 1000 1000 -1 $PLUGINSDIR\spltmp
    Pop $R1
    Pop $R1

    # Sprachauswahl anzeigen.
    !insertmacro MUI_LANGDLL_DISPLAY

    StrCpy $sub_folder "Kalypso_${VERSION}"
FunctionEnd

# Uninstaller Funktionen.
Function un.onInit
    !insertmacro MUI_UNGETLANGUAGE

    # Pr�fe, ob die Config-Datei f�r den Installer vorhanden ist.
    # $INSTDIR ist im Moment noch der Pfad des Uninstallers.
    ${If} ${FileExists} $INSTDIR\kalypso_uninstall.ini
      # OK, die Datei ist hier.
    ${Else}
      MessageBox MB_OK|MB_ICONEXCLAMATION "$(noini)"
      Quit
    ${EndIf}

    # INI - Werte auslesen.
    Push $R0

    ReadINIStr $R0 "$INSTDIR\kalypso_uninstall.ini" "Kalypso" "Path"
    ReadINIStr $startmenu_path "$INSTDIR\kalypso_uninstall.ini" "Startmenu" "Path"
    ReadINIStr $desktop_icon "$INSTDIR\kalypso_uninstall.ini" "Icons" "Desktop"
    ReadINIStr $quicklaunch_icon "$INSTDIR\kalypso_uninstall.ini" "Icons" "Quicklaunch"
    ReadINIStr $startmenu_icons "$INSTDIR\kalypso_uninstall.ini" "Icons" "Startmenu"

    # Nun sollte in $INSTDIR der neue Pfad sein.
    StrCpy $INSTDIR $R0
    Pop $R0

    StrCpy $sub_folder "Kalypso_${VERSION}"

    IfErrors error
    Goto ok

    error:
      MessageBox MB_OK|MB_ICONEXCLAMATION $(errini)
      Quit

    ok:
      # Pr�fe, ob es eine Installation von kalypso ist.
      call un.checkInst

      # Pr�fe, ob es sich um ein kritisches Verzeichnis handelt, das gel�scht werden soll.
      call un.checkCriticalPaths

    # Alle Sektionen sollen ausgef�hrt werden, ansonsten wird ein Makro zum ausw�hlen
    # der Sektionen gebraucht, die installiert sind.
    # Hier sollen keine Registrierungs-Eintr�ge verwendet werden.
    # Deswegen werden immer beide aufgerufen.
    !insertmacro SelectSection "${UNSEC0000}"
FunctionEnd

;--------------------------------------------
# Funktionen.
;--------------------------------------------
Function checkCriticalInstallPath
  # Setze auf False.
  StrCpy $critical_paths "FALSE"

  #--- Install-Pfad pr�fen.
  ${if} "$INSTDIR" == "$PROGRAMFILES"
    StrCpy $critical_paths "TRUE"
  ${EndIf}

  ${if} "$INSTDIR" == "$WINDIR"
    StrCpy $critical_paths "TRUE"
  ${EndIf}

  ${if} $critical_paths == "TRUE"
    MessageBox MB_OK|MB_ICONEXCLAMATION "$(warninst3)"
    Abort
  ${EndIf}
FunctionEnd

Function un.checkCriticalPaths
  # Setze auf False.
  StrCpy $critical_paths "FALSE"

  #--- Install-Pfad pr�fen.
  ${if} "$INSTDIR" == "$PROGRAMFILES"
    StrCpy $critical_paths "TRUE"
  ${EndIf}

  ${if} "$INSTDIR" == "$WINDIR"
    StrCpy $critical_paths "TRUE"
  ${EndIf}

  # Show message
  ${if} $critical_paths == "TRUE"
    MessageBox MB_OK|MB_ICONEXCLAMATION "$(warninst4)"
    Quit
  ${EndIf}
FunctionEnd

Function checkDirectory
    # Pr�fe, ob das Verzeichnis bereits existiert.
    ${If} ${FileExists} "$INSTDIR\$sub_folder\*.*"
	    MessageBox MB_YESNO|MB_ICONQUESTION|MB_DEFBUTTON2 \
	    "$(warndir)" \
		IDYES DirOk
	    Abort
    ${EndIf}

    DirOk:
FunctionEnd

Function checkInst
    # Pr�fe, ob es bereits eine Installation von kalypso in diesem Verzeichnis gibt.
    ${If} ${FileExists} "$INSTDIR\$sub_folder\kalypso.exe"
	    MessageBox MB_YESNO|MB_ICONQUESTION|MB_DEFBUTTON2 \
	    "$(warninst)" \
		IDYES DirOk
	    Abort
    ${EndIf}

    DirOk:
FunctionEnd

Function un.checkInst
    # Pr�fe, ob es eine Installation von kalypso ist.
    ${If} ${FileExists} "$INSTDIR\$sub_folder\kalypso.exe"
      # OK, die Datei ist hier.
    ${Else}
      MessageBox MB_OK|MB_ICONEXCLAMATION "$(warninst2)"
      Quit
    ${EndIf}
FunctionEnd

Function cb_PageData
  # Die Seite zur Auswahl des Daten-Pfades holen.
  Push $R0

  !insertmacro MUI_HEADER_TEXT "$(opt_title)" "$(opt_text)"
  InstallOptions::dialog "$PLUGINSDIR\page_data.ini"
  Pop $R0

  Pop $R0
FunctionEnd

Function pre_DirPage
    # Setze Text f�r die Optionen-Seite entsprechend der Sprache.
    !insertmacro MUI_INSTALLOPTIONS_WRITE "page_data.ini" "Field 1" "Text" $(opt_page1)
    !insertmacro MUI_INSTALLOPTIONS_WRITE "page_data.ini" "Field 2" "Text" $(opt_page2)
    !insertmacro MUI_INSTALLOPTIONS_WRITE "page_data.ini" "Field 3" "Text" $(opt_page3)

    # Falls die Sektion f�r kalypso nicht ausgew�hlt wurde,
    # Die Directory-Seite �berspringen.
    SectionGetFlags ${SEC0000} $0
    IntOp $0 $0 & ${SF_SELECTED}

    ${If} $0 == "0"
      abort
    ${EndIf}
FunctionEnd

Function leave_DirPage
    # Pr�fe, ob es sich um ein kritisches Verzeichnis handelt.
    call checkCriticalInstallPath

    # Pr�fe ob es bereits eine Installation in diesem Verzeichnis gibt.
    call checkInst

    # Pr�fe ob das Verzeichnis bereits existiert.
    call checkDirectory
FunctionEnd

Function pre_StartmenuPage
    # Falls keine Startmen�-Eintr�ge vorgenommen werden sollen,
    # die Startmen�-Seite �berspringen.
    ${if} $startmenu_icons == "0"
      abort
    ${EndIf}

    # Falls die Sektion f�r kalypso nicht ausgew�hlt wurde,
    # die Startmen�-Seite �berspringen.
    SectionGetFlags ${SEC0000} $0
    IntOp $0 $0 & ${SF_SELECTED}

    ${If} $0 == "0"
      abort
    ${EndIf}
FunctionEnd

Function leave_DataPage
    ReadINIStr $desktop_icon "$PLUGINSDIR\page_data.ini" "Field 1" "State"
    ReadINIStr $quicklaunch_icon "$PLUGINSDIR\page_data.ini" "Field 2" "State"
    ReadINIStr $startmenu_icons "$PLUGINSDIR\page_data.ini" "Field 3" "State"
FunctionEnd

; Push $filenamestring (e.g. 'c:\this\and\that\filename.htm')
; Push "\"
; Call StrSlash
; Pop $R0
; ;Now $R0 contains 'c:/this/and/that/filename.htm'
;Function StrSlash
;  Exch $R3 ; $R3 = needle ("\" or "/")
;  Exch
;  Exch $R1 ; $R1 = String to replacement in (haystack)
;  Push $R2 ; Replaced haystack
;  Push $R4 ; $R4 = not $R3 ("/" or "\")
;  Push $R6
;  Push $R7 ; Scratch reg
;  StrCpy $R2 ""
;  StrLen $R6 $R1
;  StrCpy $R4 "\"
;  StrCmp $R3 "/" loop
;  StrCpy $R4 "/"
;loop:
;  StrCpy $R7 $R1 1
;  StrCpy $R1 $R1 $R6 1
;  StrCmp $R7 $R3 found
;  StrCpy $R2 "$R2$R7"
;  StrCmp $R1 "" done loop
;found:
;  StrCpy $R2 "$R2$R4"
;  StrCmp $R1 "" done loop
;done:
;  StrCpy $R3 $R2
;  Pop $R7
;  Pop $R6
;  Pop $R4
;  Pop $R2
;  Pop $R1
;  Exch $R3
;FunctionEnd

# Installer Language Strings
LangString ^UninstallLink ${LANG_ENGLISH} "Uninstall $sub_folder"
LangString ^UninstallLink ${LANG_GERMAN} "Entferne $sub_folder"

# Beschreibungen.

# Eine mehrsprachige Beschreibung der Sektion 1.
LangString Sec1 ${LANG_ENGLISH} "Kalypso - Program"
LangString Sec1 ${LANG_GERMAN} "Kalypso - Programm"

# Die Beschreibungen den Sektionen zu ordnen.
!insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
!insertmacro MUI_DESCRIPTION_TEXT ${SEC0000} $(Sec1)
!insertmacro MUI_FUNCTION_DESCRIPTION_END