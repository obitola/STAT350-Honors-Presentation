data <- read.table("/home/tobi/Stat350/Data/Generator/data8.txt", header = TRUE, sep = "\t")

data$Type <- as.factor(data$Type)

data <- subset(data,
		 #Type == "ctrl" | Type == "syn1" | Type == "syn2" | Type == "syn3",
		 Type == "syn1" | Type == "syn2" | Type == "syn3",
		 select = c("Type", "Time"))

library(ggplot2)
# Effects Plot
dev.new()
ggplot(data = data, aes(x = Type, y = Time)) +
	stat_summary(fun.y = mean, geom = "point") +
	stat_summary(fun.y = mean, geom = "line", aes(group = 1)) +
	ggtitle("data plot of Type vs Average RunTime")

tapply(data$Time, data$Type, length)
tapply(data$Time, data$Type, mean)
tapply(data$Time, data$Type, sd)

# Box Plots
ggplot(data, aes(x = Type, y = Time)) +
	geom_boxplot() +
	stat_boxplot(geom = "errorbar") +
	stat_summary(fun.y = mean, col = "black", geom = "point", size = 3) +
	ggtitle("data plot of Type vs Average RunTime")


# Histogram
xbar <- tapply(data$Time, data$Type, mean)
s <- tapply(data$Time, data$Type, sd)

data$normal.density <- apply(data, 1, function(x){
				       dnorm(as.numeric(x["Time"]),
					     xbar[x["Type"]], s[x["Type"]])})

ggplot(data, aes(x = Time)) +
	geom_histogram(aes(y = ..density..), bins = sqrt(nrow(data)) + 2,
		       fill = "grey", col = "black") +
		 facet_grid(Type ~ .) +
		 geom_density(col = "red", lwd = 1) +
		 geom_line(aes(y = normal.density), col = "blue", lwd = 1) +
		ggtitle("data plot of Type vs Average RunTime")

# QQ plot
data$intercept <- apply(data, 1, function(x){xbar[x["Type"]]})
data$slope <- apply(data, 1, function(x){s[x["Type"]]})

ggplot(data, aes(sample = Time)) +
	stat_qq() +
	facet_grid(Type ~ .) +
	geom_abline(data = data, aes(intercept = intercept, slope = slope)) +
	ggtitle("Histograms of Test Scores by Type")

# ANOVA
fit <- aov(Time ~ Type, data = data)
summary(fit)

test.Tukey <- TukeyHSD(fit, conf.level = 0.95)
test.Tukey

# Bonus
library(multcomp)
fit <- aov(Time ~ Type, data = data)
Dunnet <- glht(fit, linfct=mcp(Type="Dunnett"))
summary(Dunnet)


# Import Data
comp <- read.table("/home/tobi/Stat350/Data/Generator/compare.txt", header = TRUE, sep = "\t")

comp$normal.density <- ifelse(comp$Type == "ctrl",
			      dnorm(comp$Time, xbar["ctrl"], s["ctrl"]),
			      dnorm(comp$Time, xbar["data"], s["data"]))

# Histograms
ggplot(comp, aes(x = Time)) +
	geom_histogram(aes(y = ..density..),
		       bins = 100,
		       fill = "grey", col = "black") +
facet_grid(Type ~ .) +
geom_density(col = "red", lwd = 1) +
geom_line(aes(y = normal.density), col = "blue", lwd = 1) +
ggtitle("Histogram")

# Boxplots
ggplot(comp, aes(x = Type, y = Time)) +
	geom_boxplot() + 
	stat_boxplot(geom = "errorbar") +
	stat_summary(fun.y = mean, col = "black", geom = "point", size = 3) +
	ggtitle("Boxplots")

xbar <- tapply(comp$Time, comp$Type, mean)
s <- tapply(comp$Time, comp$Type, sd)


# QQ plot
comp$intercept <- apply(comp, 1, function(x){xbar[x["Type"]]})
comp$slope <- apply(comp, 1, function(x){s[x["Type"]]})

ggplot(comp, aes(sample = Time)) +
	stat_qq() +
	facet_grid(Type ~ .) +
	geom_abline(data = comp, aes(intercept = intercept, slope = slope)) +
	ggtitle("Histograms of Test Scores by Type")

# T-Test
t.test(comp$Time ~ comp$Type, mu = 0, conf.level = 0.95, paired = FALSE, alternative = "two.sided", var.equal = FALSE)
