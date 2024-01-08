import os

# Set the base path for the source code
src_path = 'C:/Users/admin/projects/mySsearch/src'

# Set the output file path where you want to save all the code
output_path = 'C:/Users/admin/projects/mySsearch/all_code.txt'

# Function to recursively collect code from files
def collect_code(base_path, output_file):
    # Open the output file once and write all code to it
    with open(output_file, 'w', encoding='utf-8') as outfile:
        # os.walk will allow us to walk through all subdirectories and files
        for root, dirs, files in os.walk(base_path):
            for file in files:
                # Check for file extension, we collect `.java`, `.xml`, and `.py` files
                if file.endswith(('.java', '.xml', '.py')):
                    file_path = os.path.join(root, file)  # Full path to the file
                    try:
                        with open(file_path, 'r', encoding='utf-8') as infile:
                            # Write the path of the file and its content to the output file
                            outfile.write(f'File: {file_path}\n\n')
                            outfile.write(infile.read())
                            outfile.write('\n\n' + '-'*80 + '\n\n')
                    except Exception as e:
                        # If any file causes an error, we skip it and write the error to the output file
                        outfile.write(f'Failed to read {file_path}: {str(e)}\n\n')

# Run the function to collect the code
collect_code(src_path, output_path)

#%%
