call C:\Users\jakub\IdeaProjects\Jakub-Dudek-Kodilla-REST-project\runcrud.bat
if "%ERRORLEVEL%" == "0" goto start-browser
echo.
echo run RUNCRUD has failed - breaking work
goto fail

:start-browser
start "" http://localhost:8080/crud/v1/task/getTasks
if "%ERRORLEVEL%" == "0" goto end
echo Cannot open browser
goto fail

:fail
echo.
echo There were errors

:end
echo.
echo Work is finished (showtasks)
