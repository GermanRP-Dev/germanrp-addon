param(
    [string]$JarPath = "$PSScriptRoot/../build/libs/germanrpaddon-release.jar"
)

$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.IO.Compression.FileSystem
$archive = [System.IO.Compression.ZipFile]::OpenRead((Resolve-Path -LiteralPath $JarPath).Path)
try {
    # The production loader looks up this exact name; classes alone are insufficient.
    $entry = $archive.GetEntry('26.3-germanrpaddon.mixins.json')
    if ($null -eq $entry) {
        throw 'Missing production mixin configuration for Minecraft 26.3.'
    }
    $reader = [System.IO.StreamReader]::new($entry.Open())
    try {
        $config = $reader.ReadToEnd() | ConvertFrom-Json
    } finally {
        $reader.Dispose()
    }
    foreach ($name in @('InGameHudMixin', 'MixinSoundEngine')) {
        if ($config.client -notcontains $name) {
            throw "Mixin is not registered: $name"
        }
        $classPath = $config.package.Replace('.', '/') + "/$name.class"
        if ($null -eq $archive.GetEntry($classPath)) {
            throw "Registered mixin class is missing: $classPath"
        }
    }
    Write-Output 'PASS: Minecraft 26.3 mixin configuration and both registered classes are packaged.'
} finally {
    $archive.Dispose()
}
