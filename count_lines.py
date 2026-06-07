import os
from pathlib import Path

def count_lines_in_file(file_path):
    """Returns number of lines in file (ignores empty lines)"""
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            lines = f.readlines()
            non_empty = [l for l in lines if l.strip()]
            return len(non_empty)
    except (UnicodeDecodeError, IOError):
        return 0

def count_lines_in_directory(directory, extensions=None):
    """
    Counts lines of code in all files with given extensions.
    
    directory: path to folder
    extensions: list of extensions (e.g., ['.py', '.java', '.txt'])
                if None - counts all files
    """
    if extensions is None:
        extensions = ['.py', '.java', '.js', '.ts', '.html', '.css', '.json', '.xml']
    
    total_lines = 0
    file_stats = []
    
    for root, dirs, files in os.walk(directory):
        # Ignore hidden folders and standard cache folders
        dirs[:] = [d for d in dirs if not d.startswith('.') and d not in ['__pycache__', 'node_modules', 'venv', '.venv', 'env']]
        
        for file in files:
            file_path = Path(root) / file
            ext = file_path.suffix.lower()
            
            if ext in extensions:
                lines = count_lines_in_file(file_path)
                total_lines += lines
                file_stats.append((str(file_path), lines))
    
    return total_lines, file_stats

def print_report(directory, total_lines, file_stats):
    """Prints report"""
    print(f"\nReport for directory: {directory}")
    print(f"Total lines of code: {total_lines}")
    print(f"Files analyzed: {len(file_stats)}")
    print("\nTop 10 files by line count:")
    
    top_files = sorted(file_stats, key=lambda x: x[1], reverse=True)[:10]
    for file_path, lines in top_files:
        print(f"   {lines:6} lines | {file_path}")

if __name__ == "__main__":
    import sys
    
    if len(sys.argv) > 1:
        target_dir = sys.argv[1]
    else:
        target_dir = os.getcwd()
    
    extensions = ['.py', '.java', '.js', '.ts', '.html', '.css', '.json', '.xml', '.kt', '.go']
    
    print(f"Scanning: {target_dir}")
    total, stats = count_lines_in_directory(target_dir, extensions)
    print_report(target_dir, total, stats)