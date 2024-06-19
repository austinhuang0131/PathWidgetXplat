//
//  GroupedStationView.swift
//  widgetExtension
//
//  Created by Steven Kideckel on 2024-06-19.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import ComposeApp

struct GroupedStationView: View {
    
    let entry: SimpleEntry
    let station: WidgetData.StationData
    let width: CGFloat
    let height: CGFloat
    
    var body: some View {
        let rowCountWith6Spacing = measureRowCount(initialHeight: height, rowSpacing: 6)
        let rowCountWith4Spacing = measureRowCount(initialHeight: height, rowSpacing: 4)
        let rowCount = max(rowCountWith4Spacing, rowCountWith6Spacing)
        let rowSpacing: CGFloat = rowCountWith4Spacing > rowCountWith6Spacing ? 4 : 6
        VStack(alignment: .leading, spacing: 0) {
            HStack(spacing: 0) {
                Spacer()
                Text(
                    WidgetDataFormatter().formatHeadSign(
                        title: station.displayName,
                        fits: {
                            let titleSpace = width - 16
                            let textWidth = measureTextWidth(text: $0, font: UIFont.systemFont(ofSize: 14, weight: .bold))
                            return (textWidth <= titleSpace).toKotlinBoolean()
                        }
                    )
                )
                .multilineTextAlignment(.center)
                .font(Font.system(size: 14))
                .fontWeight(.bold)
                Spacer()
            }
                        
            let trains = station.trains
                .filter { train in !train.isPast(now: entry.date.toKotlinInstant())}
                .prefix(rowCount)
            VStack(alignment: .leading, spacing: 0) {
                ForEach(trains, id: \.id) { train in
                    Spacer().frame(height: rowSpacing)
                    HStack(alignment: .center, spacing: 0) {
                        let destination = WidgetDataFormatter().formatHeadSign(title: train.title, width: HeadSignWidth.narrow)
                        ColorCircle(size: 12, colors: train.colors)
                        Spacer().frame(width: 4)
                        Text(destination)
                            .font(Font.system(size: 12))
                            .lineLimit(1)
                        
                        let arrivalTime = formatArrivalTime(train)
                        
                        Spacer()
                        Text(arrivalTime)
                            .font(Font.system(size: 12)
                            .monospacedDigit())
                            .lineLimit(1)
                    }
                }
                
            }
            
            Spacer()
        }
        .frame(width: width, height: height)
    }
    
    private func formatArrivalTime(_ train: WidgetData.TrainData) -> String {
        var arrivalTime: String
        if (entry.configuration.timeDisplay == .clock) {
            arrivalTime = WidgetDataFormatter().formatTime(instant: train.projectedArrival)
            if (train.isBackfilled) {
                arrivalTime = "~" + arrivalTime
            }
        } else {
            arrivalTime = WidgetDataFormatter().formatRelativeTime(
                now: entry.date.toKotlinInstant(),
                time: train.projectedArrival
            )
        }
        
        return arrivalTime
    }
    
    private func measureRowCount(initialHeight: CGFloat, rowSpacing: CGFloat) -> Int {
        var height = initialHeight
        let headerHeight = measureTextHeight(text: "Updated", font: UIFont.systemFont(ofSize: 14, weight: .bold))
        height -= headerHeight
        
        let rowHeight = measureTextHeight(text: "To", font: UIFont.systemFont(ofSize: 12)) + rowSpacing
        return Int(floor(height / rowHeight))
    }
    
    private func measureTextHeight(text: String, font: UIFont) -> CGFloat {
        measureTextSize(text: text, font: font).height
    }
    
    private func measureTextWidth(text: String, font: UIFont) -> CGFloat {
        measureTextSize(text: text, font: font).width
    }
    
    private func measureTextSize(text: String, font: UIFont) -> CGRect {
        measureTextSize(maxSize: entry.size, text: text, font: font)
    }
}
