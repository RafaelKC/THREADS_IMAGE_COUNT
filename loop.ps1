param (
    [string]$nome,
    [int]$totalLoops
)

$csvPath = "./out/"+$nome+".csv"

$headers = "N_threads,MS
$headers | Out-File -FilePath $csvPath -Encoding utf8"

for ($i = 1; $i -le $totalLoops; $i++) {
    $time = java ".\src\Main.java" $i "./resources/3036x4048.jpg"

    $data = [PSCustomObject]@{
        MS = $time
        N_threads = $i
    }
    $data | Export-Csv -Path $csvPath -Append -NoTypeInformation
    
    Write-Output $time
    Write-Output "-------------------------------"
}