param (
    [string]$nome,
    [int]$idealThreads
)

$csvPath = "./out/"+$nome+".csv"
$diretorio = ".\resources"

$arquivos = Get-ChildItem -Path $diretorio | Where-Object { $_.PSIsContainer -eq $false } | Select-Object -ExpandProperty Name

$headers = "Resolution,UMT,IDEAL_T,IDEAL
$headers | Out-File -FilePath $csvPath -Encoding utf8"

foreach ($arquivo in $arquivos) {
    $filepath = "./resources/${arquivo}"
    $time1 = java ".\src\Main.java" 1 $filepath
    $timeIdeal = java ".\src\Main.java" $idealThreads $filepath

    $filenameWithoutExtension = $arquivo -replace '\.[^.]*$'

    $data = [PSCustomObject]@{
        Resolution = $filenameWithoutExtension
        UMT = $time1
        IDEAL_T = $timeIdeal
        IDEAL = $idealThreads
    }
    $data | Export-Csv -Path $csvPath -Append -NoTypeInformation

    
    Write-Output $filenameWithoutExtension
    Write-Output "1 Thread" $time1
    Write-Output "Ideal: " $timeIdeal
    Write-Output "-------------------------------"
}