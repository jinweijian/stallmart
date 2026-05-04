import { copyFileSync, existsSync, mkdirSync, readFileSync, rmSync, statSync, writeFileSync } from 'node:fs'
import { tmpdir } from 'node:os'
import { basename, join, resolve } from 'node:path'
import { spawnSync } from 'node:child_process'
import { fileURLToPath } from 'node:url'

const projectRoot = resolve(fileURLToPath(new URL('..', import.meta.url)))
const workspaceRoot = resolve(projectRoot, '..')
const tabbarFiles = [
  'home.png',
  'home-active.png',
  'cart.png',
  'cart-active.png',
  'order.png',
  'order-active.png',
  'my.png',
  'my-active.png',
]

const options = parseArgs(process.argv.slice(2))
const sourceDir = resolve(projectRoot, options.sourceDir || '../tmp/image2')
const outputDir = resolve(projectRoot, options.outputDir || 'src/static/tabbar')
const webMirrorDir = resolve(workspaceRoot, 'web/public/static/tabbar')
const maxBytes = Number(options.maxBytes || 39500)
const minSize = Number(options.minSize || 81)
const maxSize = Number(options.maxSize || 320)
const paddingRatio = Number(options.paddingRatio || 0.04)
const syncDist = options.syncDist !== false
const mirrorWeb = options.mirrorWeb !== false && existsSync(webMirrorDir)

if (process.platform !== 'win32') {
  throw new Error('optimize:tabbar currently requires Windows PowerShell and System.Drawing')
}

if (!existsSync(sourceDir)) {
  throw new Error(`Source directory not found: ${sourceDir}`)
}

for (const file of tabbarFiles) {
  const sourcePath = join(sourceDir, file)
  if (!existsSync(sourcePath)) {
    throw new Error(`Missing required tabBar source icon: ${sourcePath}`)
  }
}

mkdirSync(outputDir, { recursive: true })
if (mirrorWeb) mkdirSync(webMirrorDir, { recursive: true })

const tempRoot = join(tmpdir(), `stallmart-tabbar-${Date.now()}`)
mkdirSync(tempRoot, { recursive: true })

try {
  const psScriptPath = join(tempRoot, 'optimize-tabbar.ps1')
  const optimizedDir = join(tempRoot, 'optimized')
  mkdirSync(optimizedDir, { recursive: true })
  writeFileSync(psScriptPath, buildPowerShellScript(), 'utf8')

  const ps = spawnSync(
    'powershell',
    [
      '-NoProfile',
      '-ExecutionPolicy',
      'Bypass',
      '-File',
      psScriptPath,
      '-SourceDir',
      sourceDir,
      '-OutputDir',
      optimizedDir,
      '-FilesCsv',
      tabbarFiles.join(','),
      '-MaxBytes',
      String(maxBytes),
      '-MinSize',
      String(minSize),
      '-MaxSize',
      String(maxSize),
      '-PaddingRatio',
      String(paddingRatio),
    ],
    { encoding: 'utf8' },
  )

  if (ps.status !== 0) {
    throw new Error(`PowerShell tabBar optimizer failed:\n${ps.stderr || ps.stdout}`)
  }

  const rows = []
  for (const file of tabbarFiles) {
    const optimizedPath = join(optimizedDir, file)
    const stats = statSync(optimizedPath)
    if (stats.size > maxBytes) {
      throw new Error(`${file} is still above ${maxBytes} bytes: ${stats.size}`)
    }

    const { width, height } = readPngSize(optimizedPath)
    copyFileSync(optimizedPath, join(outputDir, file))
    if (mirrorWeb) copyFileSync(optimizedPath, join(webMirrorDir, file))
    rows.push({ file, width, height, bytes: stats.size })
  }

  if (syncDist) {
    const syncScript = resolve(projectRoot, 'scripts/sync-weapp-static.mjs')
    const sync = spawnSync(process.execPath, [syncScript], { cwd: projectRoot, encoding: 'utf8' })
    if (sync.status !== 0) {
      throw new Error(`Static sync failed:\n${sync.stderr || sync.stdout}`)
    }
  }

  console.log(`Optimized tabBar icons from ${sourceDir}`)
  console.log(`Output: ${outputDir}`)
  if (mirrorWeb) console.log(`Mirrored: ${webMirrorDir}`)
  console.table(rows.map((row) => ({
    file: row.file,
    size: `${row.width}x${row.height}`,
    bytes: row.bytes,
    kb: (row.bytes / 1024).toFixed(1),
  })))
} finally {
  rmSync(tempRoot, { recursive: true, force: true })
}

function parseArgs(args) {
  const parsed = {
    sourceDir: undefined,
    outputDir: undefined,
    maxBytes: undefined,
    minSize: undefined,
    maxSize: undefined,
    paddingRatio: undefined,
    syncDist: true,
    mirrorWeb: true,
  }

  for (const arg of args) {
    if (arg === '--no-dist') {
      parsed.syncDist = false
      continue
    }
    if (arg === '--no-web') {
      parsed.mirrorWeb = false
      continue
    }
    if (arg.startsWith('--out=')) {
      parsed.outputDir = arg.slice('--out='.length)
      continue
    }
    if (arg.startsWith('--max-bytes=')) {
      parsed.maxBytes = arg.slice('--max-bytes='.length)
      continue
    }
    if (arg.startsWith('--min-size=')) {
      parsed.minSize = arg.slice('--min-size='.length)
      continue
    }
    if (arg.startsWith('--max-size=')) {
      parsed.maxSize = arg.slice('--max-size='.length)
      continue
    }
    if (arg.startsWith('--padding-ratio=')) {
      parsed.paddingRatio = arg.slice('--padding-ratio='.length)
      continue
    }
    if (!parsed.sourceDir) {
      parsed.sourceDir = arg
      continue
    }
    throw new Error(`Unknown argument: ${arg}`)
  }

  return parsed
}

function readPngSize(filePath) {
  const header = readFileSync(filePath)
  const signature = header.subarray(0, 8).toString('hex')
  if (signature !== '89504e470d0a1a0a') {
    throw new Error(`${basename(filePath)} is not a PNG file`)
  }

  return {
    width: header.readUInt32BE(16),
    height: header.readUInt32BE(20),
  }
}

function buildPowerShellScript() {
  return String.raw`
param(
  [Parameter(Mandatory = $true)][string]$SourceDir,
  [Parameter(Mandatory = $true)][string]$OutputDir,
  [Parameter(Mandatory = $true)][string]$FilesCsv,
  [Parameter(Mandatory = $true)][int]$MaxBytes,
  [Parameter(Mandatory = $true)][int]$MinSize,
  [Parameter(Mandatory = $true)][int]$MaxSize,
  [Parameter(Mandatory = $true)][double]$PaddingRatio
)

$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Drawing
New-Item -ItemType Directory -Path $OutputDir -Force | Out-Null

function Save-ResizedPng {
  param(
    [Parameter(Mandatory = $true)][string]$SourcePath,
    [Parameter(Mandatory = $true)][string]$TargetPath,
    [Parameter(Mandatory = $true)][int]$CanvasSize
  )

  $img = [System.Drawing.Image]::FromFile($SourcePath)
  try {
    $sourceBitmap = New-Object System.Drawing.Bitmap $img
    try {
      $minX = $sourceBitmap.Width
      $minY = $sourceBitmap.Height
      $maxX = -1
      $maxY = -1

      for ($y = 0; $y -lt $sourceBitmap.Height; $y++) {
        for ($x = 0; $x -lt $sourceBitmap.Width; $x++) {
          $pixel = $sourceBitmap.GetPixel($x, $y)
          if ($pixel.A -gt 10) {
            if ($x -lt $minX) { $minX = $x }
            if ($y -lt $minY) { $minY = $y }
            if ($x -gt $maxX) { $maxX = $x }
            if ($y -gt $maxY) { $maxY = $y }
          }
        }
      }

      if ($maxX -lt 0) {
        throw "No visible alpha pixels found in $SourcePath"
      }

      $sourceRect = New-Object System.Drawing.Rectangle $minX, $minY, ($maxX - $minX + 1), ($maxY - $minY + 1)
      $padding = [Math]::Max(1, [Math]::Floor($CanvasSize * $PaddingRatio))
      $targetBox = $CanvasSize - ($padding * 2)
      if ($targetBox -lt 1) {
        throw "Canvas size $CanvasSize is too small for padding ratio $PaddingRatio"
      }

    $canvas = New-Object System.Drawing.Bitmap $CanvasSize, $CanvasSize, ([System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    try {
      $canvas.SetResolution(144, 144)
      $g = [System.Drawing.Graphics]::FromImage($canvas)
      try {
        $g.Clear([System.Drawing.Color]::Transparent)
        $g.CompositingQuality = [System.Drawing.Drawing2D.CompositingQuality]::HighQuality
        $g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
        $g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
        $g.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality

        $scale = [Math]::Min($targetBox / $sourceRect.Width, $targetBox / $sourceRect.Height)
        $width = [Math]::Round($sourceRect.Width * $scale)
        $height = [Math]::Round($sourceRect.Height * $scale)
        $x = [Math]::Floor(($CanvasSize - $width) / 2)
        $y = [Math]::Floor(($CanvasSize - $height) / 2)
        $dest = New-Object System.Drawing.Rectangle $x, $y, $width, $height
        $g.DrawImage($img, $dest, $sourceRect, [System.Drawing.GraphicsUnit]::Pixel)
      } finally {
        $g.Dispose()
      }
      $canvas.Save($TargetPath, [System.Drawing.Imaging.ImageFormat]::Png)
    } finally {
      $canvas.Dispose()
    }
    } finally {
      $sourceBitmap.Dispose()
    }
  } finally {
    $img.Dispose()
  }
}

$files = $FilesCsv.Split(',')
foreach ($file in $files) {
  $source = Join-Path $SourceDir $file
  $target = Join-Path $OutputDir $file
  $candidate = Join-Path $OutputDir "$file.candidate.png"
  $matched = $false

  for ($size = $MaxSize; $size -ge $MinSize; $size--) {
    Save-ResizedPng -SourcePath $source -TargetPath $candidate -CanvasSize $size
    $bytes = (Get-Item -LiteralPath $candidate).Length
    if ($bytes -le $MaxBytes) {
      Move-Item -LiteralPath $candidate -Destination $target -Force
      $matched = $true
      break
    }
    Remove-Item -LiteralPath $candidate -Force
  }

  if (-not $matched) {
    throw "Could not fit $file under $MaxBytes bytes between $MinSize and $MaxSize pixels"
  }
}
`
}
