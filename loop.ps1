$csvPath = "./out/output.csv"

$headers = "N_threads,MS
$headers | Out-File -FilePath $csvPath -Encoding utf8"

for ($i = 1; $i -le 20; $i++) {
    $time = java ".\src\Main.java" $i
    Write-Output $rsult

    $data = [PSCustomObject]@{
        MS = $time
        N_threads = $i
    }
    $data | Export-Csv -Path $csvPath -Append -NoTypeInformation
    Write-Output "-------------------------------"
    Write-Output $time
}