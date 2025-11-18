platform :ios, '16.3'

target 'news' do
  use_frameworks!

  # Pods for news
  pod 'GRDB.swift', git: 'https://github.com/groue/GRDB.swift.git', tag: 'v7.8.0'

  target 'newsTests' do
    inherit! :search_paths
    # Pods for testing
    pod 'MockingbirdFramework', '~> 0.20'
  end

  target 'newsUITests' do
    # Pods for UI testing
  end
end

