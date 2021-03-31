`timescale 1ns / 1ps
//////////////////////////////////////////////////////////////////////////////////
// Company: 
// Engineer: 
// 
// Create Date: 03/17/2021 08:36:49 PM
// Design Name: 
// Module Name: function
// Project Name: 
// Target Devices: 
// Tool Versions: 
// Description: 
// 
// Dependencies: 
// 
// Revision:
// Revision 0.01 - File Created
// Additional Comments:
// 
//////////////////////////////////////////////////////////////////////////////////


module main_function(
    input clk_i,    
    input [7:0] a_bi,
    input [7:0] b_bi,
    input start_i,
    input rst_i,
    output reg [23:0] result_bo,
    output busy_o
);
    localparam IDLE = 1'b0;
    localparam WORK = 1'b1; 
    reg [7:0] a;
    reg [15:0] a_mult;
    reg [7:0] b;
    reg [23:0] part_result;
    reg state;
    reg start_cube;
    reg start_mult;
    wire [23:0] cube_result;
    wire [23:0] mult_result;
    wire [1:0] cube_busy;
    wire mult_busy;
    wire done;
    cube cube1(
        .clk_i(clk_i),
        .rst_i(rst_i),
        .a_bi(a),
        .start_i(start_cube),
        .busy_o(cube_busy),
        .y_bo(cube_result)
    );
    
    mult mult1 (
        .clk_i(clk_i),
        .rst_i(rst_i),
        .a_bi(a_mult),
        .b_bi(b),
        .start_i(start_mult),
        .busy_o(mult_busy),
        .y_bo(mult_result)
    );
    assign busy_o = state;
    assign done = mult_busy == 0 && cube_busy == 0 && 
    (mult_result != 0 && a_mult != 0 && b!=0 || mult_result == 0 && a_mult == 0 && b==0 ) && 
    (cube_result != 0 && a!= 0 || cube_result == 0 && a== 0);
    always @(posedge clk_i) begin
        if(rst_i) begin
            state <= IDLE;
            result_bo <= 0;
        end else begin
            case(state)
                IDLE:
                    if(start_i) begin
                        part_result <= 0;
                        a <= a_bi;
                        a_mult <= {8'b0, a_bi};
                        b <= b_bi;
                        state <= WORK;
                        start_cube <= 1;
                        start_mult <= 1;
                    end
                WORK:
                    begin
                        if(done) begin
                            state <= IDLE;
                            result_bo <= cube_result + mult_result;
                        end 
                    end      
            endcase
        end
    end
    always @(mult_busy) begin
        if(mult_busy == 1) start_mult <= 0;
    end
    always @(cube_busy) begin
        if(cube_busy == 1) start_cube <= 0;
    end
endmodule
